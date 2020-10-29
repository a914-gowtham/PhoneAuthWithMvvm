package com.gowtham.firebasephoneauthwithmvvm.fragments.countries

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.gowtham.firebasephoneauthwithmvvm.R
import com.gowtham.firebasephoneauthwithmvvm.databinding.RowCountryBinding
import com.gowtham.firebasephoneauthwithmvvm.models.Country
import com.gowtham.firebasephoneauthwithmvvm.utils.Countries
import java.util.*
import kotlin.collections.ArrayList

interface ItemClickListener {
    fun onItemClicked(v: View, position: Int)
}

class AdCountries : RecyclerView.Adapter<AdCountries.UserViewModel>() {

    lateinit var countries: ArrayList<Country>

    private lateinit var  allCountries: ArrayList<Country>

    fun setData() {
        this.countries = Countries.getCountries() as ArrayList<Country>
        allCountries= ArrayList()
        allCountries.addAll(countries)
    }

    companion object {
        var itemClickListener: ItemClickListener? = null
    }

    fun filter(query: String) {
        try {
            countries.clear()
            if (query.isEmpty())
                countries.addAll(allCountries)
            else {
                for (country in allCountries) {
                    if (country.name.toLowerCase(Locale.getDefault())
                            .contains(query.toLowerCase(Locale.getDefault()))
                    )
                        countries.add(country)
                }
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewModel {
        val binding: RowCountryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.row_country, parent, false
        )
        return UserViewModel(binding)
    }


    override fun onBindViewHolder(holder: UserViewModel, position: Int) {
        holder.bind(countries[position])
    }

    class UserViewModel(val binding: RowCountryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Country) {
            binding.country = item
            binding.viewRoot.setOnClickListener { v ->
                itemClickListener?.onItemClicked(v,adapterPosition)
            }
            binding.executePendingBindings()
        }
    }

    override fun getItemCount() = countries.size

}
