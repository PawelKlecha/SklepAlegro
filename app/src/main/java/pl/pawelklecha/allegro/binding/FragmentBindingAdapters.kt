package pl.pawelklecha.allegro.binding

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import pl.pawelklecha.allegro.R
import pl.pawelklecha.allegro.api.model.Price

/**
 * BindingAdapters for fragments, where context is required.
 */
class FragmentBindingAdapters(private val fragment: Fragment) {

    // Loading image with Glide
    @BindingAdapter("imageUrl", "imageRequestListener", requireAll = false)
    fun bindImage(imageView: ImageView, url: String?, listener: RequestListener<Drawable?>?) {
        Glide.with(fragment).load(url).listener(listener).into(imageView)
    }

    // Price converter for TextView, that makes amount after comma smaller.
    @BindingAdapter("priceText")
    fun priceText(textView: TextView, price: Price) {
        val priceString = String.format("%.2f", price.amount)
        val splicedDouble = priceString.split(",", ".")
        val styledText =
            SpannableString("${splicedDouble[0]},${splicedDouble[1]} ${price.currency}")

        styledText.setSpan(
            TextAppearanceSpan(fragment.context, R.style.BigPrice),
            0,
            splicedDouble[0].length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        styledText.setSpan(
            TextAppearanceSpan(fragment.context, R.style.SmallPrice),
            splicedDouble[0].length,
            styledText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView.setText(styledText, TextView.BufferType.SPANNABLE)
    }
}