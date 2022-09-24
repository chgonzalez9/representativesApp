package com.example.android.politicalpreparedness.election.info

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.election.elections.ElectionsViewModel

class VoterInfoFragment : Fragment() {

    private val _viewModel: VoterInfoViewModel by lazy {
        ViewModelProvider(this,
        VoterInfoViewModelFactory(electionId, division, dataSource)
        )[VoterInfoViewModel::class.java]
    }

    private val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
    private val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision

    private val dataSource = ElectionDatabase.getInstance(requireContext()).electionDao

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.voterInfoViewModel = _viewModel

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
        */


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root
    }

    //TODO: Create method to load URL intents

}