package com.example.asteroidradarapp.main

import android.accounts.AccountManager.get
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatDrawableManager.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner.get
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.asteroidradarapp.Constants
import com.example.asteroidradarapp.R
import com.example.asteroidradarapp.databinding.FragmentMainBinding
import com.example.asteroidradarapp.network.NasaApiFilter
import com.squareup.picasso.Picasso
import timber.log.Timber



class MainFragment : Fragment() {

    private lateinit var asteroidAdapter: MainAdapter

    private val viewModel: MainViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onViewCreated()"
        }
        ViewModelProvider(this, MainViewModelFactory(activity.application)).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        /**
         * Adapter to handle click
         * */
        asteroidAdapter = MainAdapter(MainAdapter.OnClickListener{asteroid ->
            viewModel.displayAsteroidDetails(asteroid)
        })

        // Use the RecyclerView
        binding.asteroidRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.asteroidRecycler.adapter = asteroidAdapter

        viewModel.navigateToSelectedAsteroid.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        })

        /**
         * Load pic of day
         * */
        viewModel.picOfDay.observe(viewLifecycleOwner, Observer {pictureOfDay ->

            if (pictureOfDay != null && pictureOfDay.mediaType == Constants.MEDIA_TYPE) {
                Picasso.with(requireContext()).load(pictureOfDay.url).into(binding.activityMainImageOfTheDay)
            }
            else {
                Picasso.with(context).load(R.drawable.asteroid_safe).into(binding.activityMainImageOfTheDay)
            }

        })

        /**
         * Submit list item to recyclerview to display
         * */
        viewModel.asteroidList.observe(viewLifecycleOwner, Observer {
            it?.let {
                asteroidAdapter.submitList(it)
            }
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateFilter(
            when(item.itemId) {
                R.id.show_today_menu -> NasaApiFilter.SHOW_TODAY
                R.id.show_week_menu -> NasaApiFilter.SHOW_WEEK
                else -> NasaApiFilter.SHOW_SAVE
            }
        )
        return true
    }
}