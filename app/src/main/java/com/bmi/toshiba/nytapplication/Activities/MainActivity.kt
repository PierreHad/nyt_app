package com.bmi.toshiba.nytapplication.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bmi.toshiba.nytapplication.*
import com.bmi.toshiba.nytapplication.Base.BaseActivity
import com.bmi.toshiba.nytapplication.Firestore.Firestore
import com.bmi.toshiba.nytapplication.News.Adapter.NewsAdapter
import com.bmi.toshiba.nytapplication.News.NewsResponse
import com.bmi.toshiba.nytapplication.News.NewsService
import com.bmi.toshiba.nytapplication.News.Results
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import retrofit.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    // A global variable for User Name
    private lateinit var mUserfName: String
    private lateinit var mUserlName: String
    private lateinit var mUserName: String

    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)

        // This is used to align the xml view to this class
        setContentView(R.layout.activity_main)

        setupActionBar()

        nav_view.setNavigationItemSelectedListener(this)

        rv_news_list.layoutManager = LinearLayoutManager(this)
        rv_news_list.setHasFixedSize(true)
        getNewsData { news: List<Results> -> rv_news_list.adapter = NewsAdapter(news)

            showProgressDialog(resources.getString(R.string.please_wait))
            Firestore().loadUserData(this@MainActivity, true)


        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    @Deprecated("deprecated", replaceWith = ReplaceWith("Deprecated"), DeprecationLevel.WARNING )
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {

                startActivity(Intent(this@MainActivity, MyProfileActivity::class.java))
                this.finish()
            }

            R.id.nav_change_password -> {

                startActivity(Intent(this@MainActivity, ChangePasswordActivity::class.java))
                this.finish()
            }

            R.id.nav_about_us -> {

                startActivity(Intent(this@MainActivity, AboutUsActivity::class.java))
                this.finish()

            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, SignInActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            Firestore().loadUserData(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    /**
     * A function for opening and closing the Navigation Drawer.
     */
    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    /**
     * A function to get the current user details from firebase.
     */
    fun updateNavigationUserDetails(user: User, isToReadBoardsList: Boolean) {

        hideProgressDialog()

        mUserlName = user.fname
        mUserfName = user.lname
        mUserName = mUserfName + " " + mUserlName


        // The instance of the header view of the navigation view.
        val headerView = nav_view.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.iv_user_image)

        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.fname


    }

    /*private fun getNewsDetails(){
        if(Constants.isNetworkAvailable(this)){

            Toast.makeText(this@MainActivity, "You are connected to the internet.",Toast.LENGTH_SHORT).show()
            // tv_no_internet_available.visibility

            val retrofit : Retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service : NewsService = retrofit.create<NewsService>(NewsService::class.java)

            val listCall: Call<NewsResponse> = service.getNews(Constants.APP_ID)

            showProgressDialog(resources.getString(R.string.please_wait))
            listCall.enqueue(object : Callback<NewsResponse>{
                override fun onResponse(response: Response<NewsResponse>?, retrofit: Retrofit?) {
                    if(response!!.isSuccess){

                        hideProgressDialog()
                        val newsList : NewsResponse = response.body()

                    }else{
                        val rc = response.code()
                        when (rc) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "Generic Error")
                            }
                        }

                    }
                }

                override fun onFailure(t: Throwable?) {
                    hideProgressDialog()
                    Log.e("Error!", t!!.message.toString())
                }


            })

        }else{
            Toast.makeText(this@MainActivity, "You are not connected to the internet.",Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun getNewsData(callback: (List<Results>) -> Unit){
        val apiService = Constants.getInstance().create(NewsService::class.java)
        apiService.getNews().enqueue(object : Callback<NewsResponse>{
            override fun onResponse(response: Response<NewsResponse>?, retrofit: Retrofit?) {
                return callback(response!!.body().results)
            }

            override fun onFailure(t: Throwable?) {
                Toast.makeText(this@MainActivity, "News Failed to open",Toast.LENGTH_SHORT).show()
            }


        })
    }



    companion object {
        //A unique code for starting the activity for result
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

}
