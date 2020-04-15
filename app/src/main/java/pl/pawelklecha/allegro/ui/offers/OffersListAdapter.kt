package pl.pawelklecha.allegro.ui.offers


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import pl.pawelklecha.allegro.R
import pl.pawelklecha.allegro.api.model.Offer
import pl.pawelklecha.allegro.app.AppExecutors
import pl.pawelklecha.allegro.databinding.OfferItemBinding
import pl.pawelklecha.allegro.ui.common.DataBoundListAdapter

/**
 * A RecyclerView adapter for [Offer] class.
 */
class OffersListAdapter(
    private val listener: OffersAdapterListener,
    private val dataBindingComponent: DataBindingComponent,
    appExecutors: AppExecutors
) : DataBoundListAdapter<Offer, OfferItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.thumbnailUrl == newItem.thumbnailUrl
                    && oldItem.price == newItem.price
                    && oldItem.description == newItem.description
        }
    }
) {
    interface OffersAdapterListener {
        fun onOfferClicked(view: View, offer: Offer)
    }

    override fun createBinding(parent: ViewGroup): OfferItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.offer_item,
            parent,
            false,
            dataBindingComponent
        )
    }

    override fun bind(binding: OfferItemBinding, item: Offer) {
        binding.offer = item
        binding.listener = listener
    }
}
