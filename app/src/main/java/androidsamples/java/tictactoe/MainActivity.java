package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import androidsamples.java.tictactoe.model.UserViewModel;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";
  private UserViewModel userVM;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    userVM = new ViewModelProvider(this).get(UserViewModel.class);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.menu_logout) {
      Log.d(TAG, "logout clicked");
      userVM.signOut();
      Navigation.findNavController(this, R.id.nav_host_fragment).popBackStack(R.id.dashboardFragment, false);
      Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.loginFragment);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}