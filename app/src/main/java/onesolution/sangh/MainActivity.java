package onesolution.sangh;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
{
    public static String BASE_URL = "https://sangh.firebaseio.com/";
    @Bind(R.id.user_name) EditText mUserName;
    @Bind(R.id.user_pwd) EditText mUserPwd;
    @Bind(R.id.create_btn) Button mCreateBtn;
    @Bind(R.id.display_layout) LinearLayout mDisplayLayout;
    private Firebase mFirebase;
    private ArrayList<UserDataModel> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Firebase.setAndroidContext(this);
        ButterKnife.bind(MainActivity.this);
        mFirebase = new Firebase(BASE_URL);
        mUserList = new ArrayList<UserDataModel>();
        mCreateBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (mUserName.getText().toString().trim().length() > 0 && mUserName.getText().toString().trim().length() > 0)
                {

                    UserDataModel lUserDataModel = new UserDataModel();
                    lUserDataModel.setUserName(mUserName.getText().toString().trim());

                    lUserDataModel.setContact(mUserPwd.getText().toString().trim());

                    mFirebase.child("Users").push().setValue(lUserDataModel);

                    getData();


                } else
                {
                    Toast.makeText(MainActivity.this, "Your username or password is missing", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void getData()
    {

        if (mFirebase != null)
        {


            mFirebase.child("Users").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {

//                    Log.e("Data ", "@@ " + dataSnapshot.toString());
                    mDisplayLayout.removeAllViews();
                    mUserList = parseList(dataSnapshot);
//                    Log.e("Data ", "@@ " + mUserList.size());
                    for (int i = 0; i < mUserList.size(); i++)
                    {
                        TextView lTextView = new TextView(MainActivity.this);
                        lTextView.setText("" + mUserList.get(i).getUserName());
                        mDisplayLayout.addView(lTextView);
                    }

                }

                @Override public void onCancelled(FirebaseError firebaseError)
                {

                }
            });
        }
    }

    /**
     * Parsing user0
     *
     * @param dataSnapshot
     */
    public ArrayList<UserDataModel> parseList(DataSnapshot dataSnapshot)
    {
        ArrayList<UserDataModel> userModels = new ArrayList<>();
        Iterator<DataSnapshot> dataSnapshotIterator = dataSnapshot.getChildren().iterator();
        while (dataSnapshotIterator.hasNext())
        {
            UserDataModel lLL = new UserDataModel();
//            Log.e("dataSnapshotIteratorKey", "      Key====   " + dataSnapshotIterator.next().getKey());
            DataSnapshot lDataSnapshot = dataSnapshotIterator.next();
            lLL.setUserName("" + lDataSnapshot.child("userName").getValue());
            userModels.add(lLL);

        }
        return userModels;
    }

}
