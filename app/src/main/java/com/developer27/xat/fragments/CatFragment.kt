package com.developer27.xat.fragments

import com.developer27.xat.viewModels.CatViewModel
import android.R
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.developer27.xat.databinding.CatFragmentBinding
import org.json.JSONArray
import org.json.JSONObject


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class CatFragment : Fragment() {
    // Models and bindings
    private var _binding: CatFragmentBinding? = null
    private lateinit var viewModel: CatViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Relevant apps for inner app communication
    private var selectedBreedId: String? = null
    private var catList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getCatListData(requireContext())
        _binding = CatFragmentBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[CatViewModel::class.java]

        binding.getCatButton.setOnClickListener {
            val catImageView = binding.catImage
            // Check if a breed ID is selected
            if (selectedBreedId != null) {
                // Use View Model to get data
                viewModel.loadCatImage(requireContext(), selectedBreedId!!, catImageView)
                viewModel.getrestofData(requireContext(), selectedBreedId!!)

                // Use View Model to assign data
                binding.catName.text = viewModel.getCatName()
                binding.catOrigin.text = viewModel.getCatOrigin()
                binding.catDescription.text = viewModel.getCatDescription()
            } else {
                // Handle case when no breed is selected
                showMessageBox("Load Image", "No breed selected")
            }
        }

        // Initialize spinner and set adapter using view binding
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, catList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.catsList.adapter = adapter

        // Set spinner onItemSelectedListener
        binding.catsList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Update the selected breed ID when an item is selected
                selectedBreedId = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case when nothing is selected
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // method to interact with API
    fun getCatListData(context: Context) {
        // make sure to replace the fake API key below with your own
        val catUrl =
            "https://api.thecatapi.com/v1/breeds" + "?api_key=live_21dbWV3PFDB7s5CrU81b3CnwF5fDz4UyzsPPe15DD2CKAcGscBbO9DRZotLbL3jh"
        val queue = Volley.newRequestQueue(context)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
            Request.Method.GET, catUrl,
            Response.Listener<String> { response ->
                val catsArray: JSONArray = JSONArray(response)
                val catNames = ArrayList<String>()
                // Iterate through the JSON array and extract cat names
                for (i in 0 until catsArray.length()) {
                    val theCat: JSONObject = catsArray.getJSONObject(i)
                    val catID = theCat.getString("id")
                    catNames.add(catID)
                }
                // Initialize spinner and set adapter using view binding
                val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, catNames)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.catsList.adapter = adapter

                // Set spinner onItemSelectedListener
                binding.catsList.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        // Update the selected breed ID when an item is selected
                        selectedBreedId = parent?.getItemAtPosition(position).toString()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // Handle case when nothing is selected
                    }
                }
            },
            Response.ErrorListener {
                showMessageBox("MainActivity", "That didn't work!")
            }
        )
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }//end printCatData

    // Show a message box
    public fun showMessageBox(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext()) // or requireActivity() if you prefer
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            // You can perform additional actions if needed when the user clicks OK
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}