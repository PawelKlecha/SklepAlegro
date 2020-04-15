package pl.pawelklecha.allegro.ui.offers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.Hold
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.pawelklecha.allegro.R
import pl.pawelklecha.allegro.api.model.Offer
import pl.pawelklecha.allegro.api.model.Resource
import pl.pawelklecha.allegro.app.AppExecutors
import pl.pawelklecha.allegro.binding.FragmentDataBindingComponent
import pl.pawelklecha.allegro.databinding.OffersListFragmentBinding
import pl.pawelklecha.allegro.repository.PreferenceRepository
import pl.pawelklecha.allegro.ui.common.RetryCallback
import pl.pawelklecha.allegro.util.getColorFromAttr
import timber.log.Timber

/**
 * Fragment that displays offers list. I also added dark mode support and implemented new functions
 * from material design library that makes introducing animations easier.
 */
class OffersFragment : Fragment(), OffersListAdapter.OffersAdapterListener,
    Toolbar.OnMenuItemClickListener {
    private var dataBindingComponent = FragmentDataBindingComponent(this)
    private val offersViewModel by viewModel<OffersViewModel>()
    private val appExecutors: AppExecutors by inject()
    private val preferenceRepository: PreferenceRepository by inject()
    private lateinit var binding: OffersListFragmentBinding
    private lateinit var adapter: OffersListAdapter

    private var shouldShowErrorDialog = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = Hold().apply {
            duration = resources.getInteger(R.integer.motion_default_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.offers_list_fragment,
            container,
            false,
            dataBindingComponent
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        binding.lifecycleOwner = viewLifecycleOwner
        initPopupMenu()
        initRecyclerView()

        binding.retryCallback = object : RetryCallback {
            override fun retry() {
                shouldShowErrorDialog = true
                offersViewModel.refresh(true)
            }
        }
        preferenceRepository.themeModeLive.observe(viewLifecycleOwner, Observer { nightMode ->
            Timber.d(nightMode.toString())
            nightMode?.let { AppCompatDelegate.setDefaultNightMode(it) }
        })
    }

    override fun onOfferClicked(view: View, offer: Offer) {
        val directions = OffersFragmentDirections.actionOffersListToOfferDetail(offer)
        val extras = FragmentNavigatorExtras(view to view.transitionName)
        findNavController().navigate(directions, extras)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.item_theme_light -> {
                preferenceRepository.themeMode = AppCompatDelegate.MODE_NIGHT_NO
            }
            R.id.item_theme_dark -> {
                preferenceRepository.themeMode = AppCompatDelegate.MODE_NIGHT_YES
            }
            R.id.item_system_default -> {
                preferenceRepository.themeMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
        setupColorsOnPopup()
        return true
    }

    private fun initRecyclerView() {
        val rvAdapter = OffersListAdapter(
            this,
            dataBindingComponent,
            appExecutors
        )
        binding.offersList.adapter = rvAdapter
        adapter = rvAdapter

        binding.offersListResult = offersViewModel.offers
        offersViewModel.offers.observe(viewLifecycleOwner, Observer { result ->
            if (result.status == Resource.Status.ERROR && !result.data.isNullOrEmpty())
                showFetchingFromNetworkErrorDialog()
            adapter.submitList(result.data)
        })
    }

    private fun showFetchingFromNetworkErrorDialog() {
        if (shouldShowErrorDialog) {
            MaterialAlertDialogBuilder(context)
                .setTitle(resources.getString(R.string.error_network_fetch))
                .setMessage(resources.getString(R.string.error_loaded_data_from_db))
                .setPositiveButton(resources.getString(R.string.error_ok)) { _, _ -> }
                .show()
            shouldShowErrorDialog = false
        }

    }

    private fun initPopupMenu() {
        binding.toolbar.setOnMenuItemClickListener(this)
        setupColorsOnPopup()
    }

    private fun setupColorsOnPopup() {
        val menu = binding.toolbar.menu
        val defaultColor = AppCompatResources.getColorStateList(
            requireContext(),
            R.color.material_on_surface_emphasis_medium
        ).defaultColor
        val selectedItemColor = requireContext().getColorFromAttr(R.attr.colorPrimary)

        when (preferenceRepository.themeMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(0).icon, selectedItemColor)
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(1).icon, defaultColor)
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(2).icon, defaultColor)
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(0).icon, defaultColor)
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(1).icon, selectedItemColor)
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(2).icon, defaultColor)
            }
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(0).icon, defaultColor)
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(1).icon, defaultColor)
                DrawableCompat.setTint(menu.getItem(0).subMenu.getItem(2).icon, selectedItemColor)
            }
        }
    }

}
