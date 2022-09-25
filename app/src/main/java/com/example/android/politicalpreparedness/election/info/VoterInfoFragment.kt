package com.example.android.politicalpreparedness.election.info

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding

class VoterInfoFragment : Fragment() {

    private lateinit var _viewModel: VoterInfoViewModel


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val electionId = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElectionId
        val division = VoterInfoFragmentArgs.fromBundle(requireArguments()).argDivision
        val dataSource = ElectionDatabase.getInstance(requireContext()).electionDao

        _viewModel = ViewModelProvider(this,
            VoterInfoViewModelFactory(electionId, division, dataSource)
        )[VoterInfoViewModel::class.java]

        binding.voterInfoViewModel = _viewModel

        _viewModel.votingUrl.observe(viewLifecycleOwner) {
            it?.let { url ->
                binding.stateLocations.visibility = View.VISIBLE
                binding.stateLocations.setOnClickListener {
                    intentURL(url)
                }
            }
        }

        _viewModel.ballotUrl.observe(viewLifecycleOwner) {
            it?.let{ url ->
                binding.stateBallot.visibility = View.VISIBLE
                binding.stateBallot.setOnClickListener {
                    intentURL(url)
                }
            }
        }

        _viewModel.savedElection.observe(viewLifecycleOwner) {
            it?.let {
                if (!it) {
                    binding.saveButton.text = getString(R.string.saved_button_follow)
                    binding.saveButton.setOnClickListener {
                        _viewModel.saveElection()
                    }
                } else {
                    binding.saveButton.text = getString(R.string.saved_button_unfollow)
                    binding.saveButton.setOnClickListener {
                        _viewModel.deleteElection()
                    }
                }
            }
        }

        return binding.root
    }

    private fun intentURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

}