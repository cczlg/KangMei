package com.edmond.jimi.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.edmond.jimi.Constants;
import com.edmond.jimi.KangmeiApplication;
import com.edmond.jimi.fragment.ConfigFragment;
import com.edmond.jimi.fragment.CustomerDialogFragment;
import com.edmond.jimi.fragment.CustomerListFragment;
import com.edmond.jimi.fragment.JimiMainFragment;
import com.edmond.jimi.fragment.OrderListFragment;
import com.edmond.jimi.fragment.ProductDialogFragment;
import com.edmond.jimi.fragment.ProductListFragment;
import com.edmond.jimi.util.ExportUtil;
import com.edmond.jimi.util.PrefrenceTool;
import com.edmond.kangmei.R;

public class MainActivity extends AppCompatActivity
        implements JimiMainFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private JimiMainFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private OrderListFragment reportFragement;

    Fragment fragment=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //if not found apptitle in prefrences ,show setting dialog
        String title = PrefrenceTool.getStringValue("kangmei", "apptitle",
                getApplication());
        if (title == null || title.length() == 0) {
//            dialog();
        }
        setTitle(title);

        mNavigationDrawerFragment = (JimiMainFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                fragment= OrderListFragment.newInstance(position + 1);
                break;
            case 1:
                fragment= CustomerListFragment.newInstance(position + 1);
                break;
            case 2:
                fragment= ProductListFragment.newInstance(position + 1);
                break;
            case 3:
                fragment=ConfigFragment.newInstance(position + 1);
                break;
        }
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    public void showDefault(){
        if(reportFragement==null){
            reportFragement=new OrderListFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, reportFragement)
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_order_list);
                break;
            case 2:
                mTitle = getString(R.string.title_customer);
                break;
            case 3:
                mTitle = getString(R.string.title_product);
                break;
            case 4:
                mTitle = getString(R.string.title_config);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            if(mTitle.equals(getString(R.string.title_order_list))) {
                getMenuInflater().inflate(R.menu.menu_order, menu);
            }
            if(mTitle.equals(getString(R.string.title_customer))) {
                getMenuInflater().inflate(R.menu.menu_product, menu);

            }
            if(mTitle.equals(getString(R.string.title_product))) {
                getMenuInflater().inflate(R.menu.menu_product, menu);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id==R.id.action_order){
            order();
        }
        else if (id==R.id.action_export){
            export();
        }
        else if (id==R.id.menu_new){
            if(mTitle.equals(getString(R.string.title_customer))) {
                CustomerListFragment customerListFragment= (CustomerListFragment) MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container);
                CustomerDialogFragment customerDialog = CustomerDialogFragment.newInstance(Constants.OPERATE_NEW,null);
                customerDialog.setTargetFragment(customerListFragment,1234);
                customerDialog.show(customerListFragment.getFragmentManager(),"Customer_dialog");
            }
            if(mTitle.equals(getString(R.string.title_product))) {
                ProductListFragment productListFragment= (ProductListFragment) MainActivity.this.getSupportFragmentManager().findFragmentById(R.id.container);
                ProductDialogFragment productDialog = ProductDialogFragment.newInstance(Constants.OPERATE_NEW, null);
                productDialog.setTargetFragment(productListFragment, 5678);
                productDialog.show(productListFragment.getFragmentManager(), "Product_dialog");
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(mTitle.equals(getString(R.string.title_order_list))) {
            ((OrderListFragment)fragment).reloadData();
        }
        if(mTitle.equals(getString(R.string.title_customer))) {

        }
        if(mTitle.equals(getString(R.string.title_product))) {

        }
    }

    public void order(){
        String salesman = PrefrenceTool.getStringValue("kangmei",
                "salesman", getApplicationContext());
        if (salesman == null || salesman.length() <= 0) {
            onNavigationDrawerItemSelected(3);
        } else {
            Intent intent = new Intent(MainActivity.this,
                    OrderActivity.class);
            intent.putExtra(Constants.OPERATE, Constants.OPERATE_NEW);
            startActivityForResult(intent, Constants.REQUEST_ORDER);
        }
    }

    public void export(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("确定要导出excel？ ");
        builder.setTitle("提示");

        builder.setNegativeButton("导出",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        if (ExportUtil
                                .exportExcel((KangmeiApplication) getApplication())) {
//                            groups.removeAll(groups);
//                            children.removeAll(children);
                            ((OrderListFragment)fragment).reloadData();
                        }
                        dialog.dismiss();
                    }
                });
        builder.setPositiveButton("不导出",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
}
