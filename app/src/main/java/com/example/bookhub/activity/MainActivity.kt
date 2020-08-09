package com.example.bookhub.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.widget.FrameLayout
import com.example.bookhub.*
import com.example.bookhub.fragment.AboutPhoneFragment
import com.example.bookhub.fragment.DashboardFragment
import com.example.bookhub.fragment.FavouritesFragment
import com.example.bookhub.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout:DrawerLayout
    lateinit var coordinatorLayout:CoordinatorLayout
    lateinit var toolbar: android.support.v7.widget.Toolbar
    lateinit var frameLayout:FrameLayout
    lateinit var navigationView:NavigationView

    var previousMenuItem:MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)

        setUpToolbar()


        openDrawer()

        val actionBarDrawerLayout=ActionBarDrawerToggle(this@MainActivity,
            drawerLayout,
            R.string.open_drawer, R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerLayout)
        actionBarDrawerLayout.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) //Highlighting in menu bar the selected item
            {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it


            when (it.itemId) {
                R.id.dashboard -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, DashboardFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Dashboard"
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouritesFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourites"
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ProfileFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Profile"
                }
                R.id.aboutApp -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, AboutPhoneFragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "About Phone"
                }
            }
            return@setNavigationItemSelectedListener true

        }

    }
    fun setUpToolbar()
    {
        setSupportActionBar(toolbar) //We want an action bar on top
        supportActionBar?.title="Toolbar Title "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    //Hamburger icon's setOnClickListener type method.
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id=item?.itemId

        if(id==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openDrawer()
    {
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, DashboardFragment()).commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title="Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)


    }

    override fun onBackPressed() {

        val frag=supportFragmentManager.findFragmentById(R.id.frame)
        when(frag)
        {
            !is DashboardFragment -> openDrawer()
            else ->super.onBackPressed()
        }

    }
}
