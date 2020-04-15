package pl.pawelklecha.allegro.binding

import android.os.Build
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * BindingAdapters for whole layouts, where context isn't required
 */
@BindingAdapter("visibleGone")
fun showHide(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("htmlText")
fun bindHtmlText(textView: TextView, htmlString: String?) {
    textView.text =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT)
        else
            Html.fromHtml(htmlString)
}
