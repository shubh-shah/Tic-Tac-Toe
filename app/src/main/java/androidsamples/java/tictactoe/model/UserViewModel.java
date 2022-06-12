package androidsamples.java.tictactoe.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import androidsamples.java.tictactoe.entities.Game;
import androidsamples.java.tictactoe.entities.GameResult;
import androidsamples.java.tictactoe.entities.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import lombok.Getter;

public class UserViewModel extends ViewModel {

    private final FirebaseAuth auth;

    private final DatabaseReference userDatabase;

    @Getter
    private final MutableLiveData<String> currentUserId = new MutableLiveData<>("");

    @Getter
    private final MutableLiveData<UserInfo> currentUserInfo = new MutableLiveData<>(new UserInfo(null, 0, 0, 0));

    @Getter
    private String message = "";

    public UserViewModel() {
        auth = FirebaseAuth.getInstance();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://tic-tac-toe-shubh-default-rtdb.asia-southeast1.firebasedatabase.app/");
        db.setPersistenceEnabled(true);
        userDatabase = db.getReference("users");
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser!=null) {
            currentUserId.setValue(currentUser.getUid());
        }
    }

    public boolean checkIfLoggedIn() {
        return !"".equals(currentUserId.getValue());
    }

    public void signInOrRegister(String emailId, String passwd) {
        auth.signInWithEmailAndPassword(emailId, passwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult()==null || task.getResult().getUser()==null) {
                            message = "Login Failed, null object received";
                            currentUserId.setValue("");
                        }
                        message = "Logged In Successfully";
                        currentUserId.setValue(task.getResult().getUser().getUid());
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            userRegister(emailId, passwd);
                        } catch (Exception e) {
                            message = "Login Failed, "+ e.getMessage();
                            currentUserId.setValue("");
                        }
                    }
                });
    }


    public void userRegister(String emailId, String passwd) {
        auth.createUserWithEmailAndPassword(emailId, passwd)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult()==null || task.getResult().getUser()==null) {
                            message = "Registration Failed, null object received";
                            currentUserId.setValue("");
                        }
                        message = "User Registered Successfully";
                        //Init User Info
                        userDatabase.child(task.getResult().getUser().getUid()).setValue(new UserInfo(task.getResult().getUser().getEmail(),0, 0, 0));
                        currentUserId.setValue(task.getResult().getUser().getUid());
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            message = "Registration failed: Weak Password";
                            currentUserId.setValue("");
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            message = "Registration failed: Invalid Email";
                            currentUserId.setValue("");
                        } catch (Exception e) {
                            message = "Registration failed";
                            currentUserId.setValue("");
                        }
                    }
                });
    }

    public void signOut() {
        message = "Signed Out";
        currentUserId.setValue("");
        auth.signOut();
    }

    public void retrieveUserInfo() {
        OnCompleteListener<DataSnapshot> scoreOneTimeListener = task -> {
            if (!task.isSuccessful() || task.getResult()==null) {
                message = "Couldn't Retrieve User Data";
                currentUserInfo.setValue(null);
            }
            else {
                UserInfo userInfo = task.getResult().getValue(UserInfo.class);
                if (userInfo==null) {
                    message = "Couldn't Retrieve User Data";
                    currentUserInfo.setValue(null);
                }
                else {
                    message = "";
                    currentUserInfo.setValue(userInfo);
                }
            }
        };
        if (currentUserId.getValue() == null) {
            message = "Don't have current User Id";
            currentUserInfo.setValue(null);
        }
        else {
            userDatabase.child(currentUserId.getValue()).get().addOnCompleteListener(scoreOneTimeListener);
        }
    }

    public void updateScore(GameResult result) {
        UserInfo userInfo = currentUserInfo.getValue();
        assert userInfo != null;
        if (result.equals(GameResult.Win)) {
            userInfo.registerWin();
        }
        else if(result.equals(GameResult.Loss)) {
            userInfo.registerLoss();
        }
        else {
            userInfo.registerTie();
        }
        currentUserInfo.setValue(userInfo);
        userDatabase.child(currentUserId.getValue()).setValue(userInfo);
    }
}
