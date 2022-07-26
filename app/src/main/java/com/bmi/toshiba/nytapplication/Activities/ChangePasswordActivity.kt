package com.bmi.toshiba.nytapplication.Activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bmi.toshiba.nytapplication.Base.BaseActivity
import com.bmi.toshiba.nytapplication.Constants
import com.bmi.toshiba.nytapplication.Firestore.Firestore
import com.bmi.toshiba.nytapplication.R
import com.bmi.toshiba.nytapplication.User
import com.bumptech.glide.Glide
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_my_profile.btn_update
import kotlinx.android.synthetic.main.activity_my_profile.iv_profile_user_image
import kotlinx.android.synthetic.main.dialog_progress.*
import java.io.IOException

class ChangePasswordActivity : BaseActivity() {
    private var mSelectedImageFileUri: Uri? = null
    private lateinit var mProgressDialog: Dialog

    // A global variable for user details.
    private lateinit var mUserDetails: User
    private lateinit var mUserPassword: String

    private lateinit var auth : FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        //setupActionBar()

        auth = FirebaseAuth.getInstance()

        Firestore().loadUserData(this@ChangePasswordActivity)




        //setUserPasswordInUI(mUserDetails)

        btn_update.setOnClickListener {


                updateUserPassword()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!

            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this@ChangePasswordActivity)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(iv_profile_user_image) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }




    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_my_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.change_password)
        }

        toolbar_my_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to set the existing details in UI.
     */
    fun setUserPasswordInUI(user: User) {

        // Initialize the user details variable
        mUserDetails = user

        Glide
            .with(this@ChangePasswordActivity)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)




    }




    /**
     * A function to update the user profile details into the database.
     */
    private fun updateUserPassword() {

        val userHashMap = HashMap<String, Any>()
        val user : FirebaseUser? = auth.currentUser

        if(et_old_pass != null && et_new_pass != null && et_reenter_new_pass != null){
            if(user != null){
                val credential : AuthCredential = EmailAuthProvider.getCredential(user.email!!, et_old_pass.text.toString())
                user?.reauthenticate(credential).addOnCompleteListener {
                    if (it.isSuccessful){

                        if(et_new_pass.text.toString() == et_reenter_new_pass.text.toString()) {

                            user?.updatePassword(et_new_pass.text.toString())
                                ?.addOnCompleteListener{
                                    Toast.makeText(this@ChangePasswordActivity, "Password Changed Successfully.",Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@ChangePasswordActivity, MainActivity::class.java))
                                }

                            /*userHashMap[Constants.PASSWORD] = et_reenter_new_pass.text.toString()

                            Firestore().updateUserPassword(this@ChangePasswordActivity, userHashMap)*/

                        }else{

                            Toast.makeText(this, "Please make sure you re-entered the new password correctly",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(this, "Please make sure you entered the old password correctly",Toast.LENGTH_SHORT).show()
                    }
                    }
                }else {
                Toast.makeText(this, "Authentication Unsuccessful",Toast.LENGTH_SHORT).show()
            }
            }
            //if(et_old_pass.text.toString() == Constants.PASSWORD) {
        else{
            Toast.makeText(this, "Please make sure all fields are filled!",Toast.LENGTH_SHORT).show()
        }



    }

    /**
     * A function to notify the user profile is updated successfully.
     */
    fun profileUpdateSuccess() {



        setResult(Activity.RESULT_OK)
        finish()
    }



}