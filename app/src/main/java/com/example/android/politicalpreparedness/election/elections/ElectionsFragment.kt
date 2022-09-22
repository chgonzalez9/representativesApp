package com.example.android.politicalpreparedness.election.elections

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel

class ElectionsFragment: Fragment() {

    private val _viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this)[ElectionsViewModel::class.java]
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.electionsViewModel = _viewModel

        val upcomingAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
            _viewModel.navigateToVoterInfo(it)
        })

        val savedAdapter = ElectionListAdapter(ElectionListAdapter.ElectionListener {
//            findNavController().navigate(ElectionsFragmentDirections.toVoterInfo(it.id, it.division))
            _viewModel.navigateToVoterInfo(it)
        })

        binding.upcomingElections.adapter = upcomingAdapter

        binding.savedElections.adapter = savedAdapter

        _viewModel.upcomingElections.observe(viewLifecycleOwner) {
            upcomingAdapter.submitList(it)
        }

        _viewModel.savedElections.observe(viewLifecycleOwner) {
            savedAdapter.submitList(it)
        }

        return binding.root
    }

}