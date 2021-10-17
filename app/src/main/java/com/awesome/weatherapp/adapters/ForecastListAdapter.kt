package com.awesome.weatherapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.awesome.weatherapp.R
import com.awesome.weatherapp.models.WeatherItemModel
import com.awesome.weatherapp.utilities.WeatherDateUtils
import com.awesome.weatherapp.utilities.WeatherUtils
import javax.inject.Inject

open class ForecastListAdapter @Inject constructor() :
    RecyclerView.Adapter<ForecastListAdapter.ViewHolder>() {

    private var forecastListItems: List<WeatherItemModel> = ArrayList()
    private var ctx: Context? = null


    private var mOnItemClickListener: ((view: View, day: WeatherItemModel, pos: Int) -> Unit)? =
        null


    /**
     * Sets on item click listener.
     *
     * @param mItemClickListener the m item click listener
     */
    fun setOnItemClickListener(mItemClickListener: (Any, Any, Any) -> Unit) {
        mOnItemClickListener = mItemClickListener
    }

    /**
     * Instantiates a new Adapter bills.
     *
     * @param context the context
     * @param items   the items
     */
    // Provide a suitable constructor (depends on the kind of dataset)
    fun setForecastListItemst(context: Context?, items: List<WeatherItemModel>) {
        forecastListItems = items
        ctx = context
    }

    /**
     * The type View holder.
     */
    class ViewHolder internal constructor(v: View) :
        RecyclerView.ViewHolder(v) {
        var tvDate: TextView = v.findViewById(R.id.date) as TextView
        var tvWeatherDes: TextView = v.findViewById(R.id.weather_description) as TextView
        var tvTemparature: TextView = v.findViewById(R.id.tvTemperature) as TextView
        var weatherIcon: ImageView = v.findViewById(R.id.weather_icon) as ImageView
        var baseLayout: ConstraintLayout = v.findViewById(R.id.baseLayout) as ConstraintLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create a new view
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.forecast_list_item, parent, false)
        // set the view's size, margins, paddings and layout parameters
        return ViewHolder(v)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = forecastListItems[position]
        val dateString: String =
            WeatherDateUtils.getFriendlyDateString(ctx!!, p.locationDate, false)
        val weatherTemp: String = WeatherUtils.formatTemperature(
            ctx!!,
            p.locationWeather.weatherTemp
        )
        val weatherImageId =
            WeatherUtils.getLargeArtResourceIdForWeatherCondition(p.locationWeather.weatherConditionId)
        holder.tvDate.text = dateString
        holder.tvWeatherDes.text = p.locationWeather.weatherCondition
        holder.tvTemparature.text = weatherTemp
        holder.weatherIcon.setImageResource(weatherImageId)
        holder.baseLayout.setOnClickListener {
            mOnItemClickListener?.invoke(it, p, position)
        }
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return forecastListItems.size
    }

    /**
     * Here is the key method to apply the animation
     */
    private var lastPosition = -1

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation =
                AnimationUtils.loadAnimation(ctx, R.anim.slide_in_bottom)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}

