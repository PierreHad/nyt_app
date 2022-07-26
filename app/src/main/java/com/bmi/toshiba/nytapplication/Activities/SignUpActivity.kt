package com.bmi.toshiba.nytapplication.Activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.bmi.toshiba.nytapplication.Base.BaseActivity
import com.bmi.toshiba.nytapplication.Constants
import com.bmi.toshiba.nytapplication.Firestore.Firestore
import com.bmi.toshiba.nytapplication.R
import com.bmi.toshiba.nytapplication.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.et_email
import kotlinx.android.synthetic.main.activity_sign_up.et_password

class SignUpActivity : BaseActivity() {
    /**
     * This function is auto created by Android when the Activity Class is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        //This call the parent constructor
        super.onCreate(savedInstanceState)
        // This is used to align the xml view to this class
        setContentView(R.layout.activity_sign_up)

        // This is used to hide the status bar and make the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setupActionBar()

        // Click event for sign-up button.
        btn_sign_up.setOnClickListener {
            registerUser()
        }
        switch_to_signin.setOnClickListener{
            startActivity(Intent(this@SignUpActivity, SignInActivity::class.java))
        }
    }

    /**
     * A function for actionBar Setup.
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_sign_up_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar_sign_up_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to register a user to our app using the Firebase.
     * For more details visit: https://firebase.google.com/docs/auth/android/custom-auth
     */
    private fun registerUser() {
        // Here we get the text from editText and trim the space
        val fname: String = et_fname.text.toString().trim { it <= ' ' }
        val lname: String = et_lname.text.toString().trim { it <= ' ' }
        val email: String = et_email.text.toString().trim { it <= ' ' }
        val password: String = et_password.text.toString().trim { it <= ' ' }
        val confirm_password: String = et_password_confirm.text.toString().trim { it <= ' ' }

        if (validateForm(fname, lname, email, password, confirm_password)) {
            if(password == confirm_password) {


                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(
                        OnCompleteListener<AuthResult> { task ->

                            // If the registration is successfully done
                            if (task.isSuccessful) {


                                // Firebase registered user
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                // Registered Email
                                val registeredEmail = firebaseUser.email!!

                                val user = User(
                                    firebaseUser.uid, fname, lname, registeredEmail
                                )

                                // call the registerUser function of FirestoreClass to make an entry in the database.
                                Firestore().registerUser(this@SignUpActivity, user)
                            } else {
                                Toast.makeText(
                                    this@SignUpActivity,
                                    task.exception!!.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                        })
            }else{
                Toast.makeText(this, "Please make sure you retyped the same password",Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(fname: String, lname: String, email: String, password: String, confirm_password: String): Boolean {
        return when {
            TextUtils.isEmpty(fname) -> {
                showErrorSnackBar("Please enter your first name.")
                false
            }
            TextUtils.isEmpty(lname) -> {
                showErrorSnackBar("Please enter your last name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            TextUtils.isEmpty(confirm_password) -> {
                showErrorSnackBar("Please re-enter password.")
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun userRegisteredSuccess() {

        Toast.makeText(
            this@SignUpActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        // Hide the progress dialog
        hideProgressDialog()
        startActivity(Intent(this@SignUpActivity, MainActivity::class.java))

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Sign-Up Screen
        finish()
    }

}