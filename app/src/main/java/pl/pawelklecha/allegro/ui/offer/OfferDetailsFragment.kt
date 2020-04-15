package pl.pawelklecha.allegro.ui.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import kotlinx.android.synthetic.main.offer_details_fragment.*
import pl.pawelklecha.allegro.R
import pl.pawelklecha.allegro.binding.FragmentDataBindingComponent
import pl.pawelklecha.allegro.databinding.OfferDetailsFragmentBinding
import pl.pawelklecha.allegro.util.themeInterpolator

/**
 * Based on MVVM Pattern almost every view should have his ViewModel, but there it is not necessary,
 * because it would be empty. Provided API has only one method and in the response of it,
 * we can find a list with offers and every detail about each of them. The right way to do it
 * would be creating a second API method that should return us details about the specified
 * offer(we can achieve that by passing offer_id) and creating a ViewModel for OfferDetails class
 */

class OfferDetailsFragment : Fragment() {
    private var dataBindingComponent: DataBindingComponent = FragmentDataBindingComponent(this)
    private val params by navArgs<OfferDetailsFragmentArgs>()
    private lateinit var binding: OfferDetailsFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.offer_details_fragment,
            container,
            false,
            dataBindingComponent
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.offerDetails = params.offerDetails

        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        startTransitions()
    }

    private fun prepareTransitions() {
        postponeEnterTransition()
        sharedElementEnterTransition = MaterialContainerTransform(requireContext()).apply {
            duration = resources.getInteger(R.integer.motion_default_large).toLong()
            interpolator = requireContext().themeInterpolator(R.attr.motionInterpolatorPersistent)
        }
    }

    private fun startTransitions() {
        binding.executePendingBindings()
        startPostponedEnterTransition()
    }

}