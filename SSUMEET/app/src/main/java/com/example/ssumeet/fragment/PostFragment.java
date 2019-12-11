package com.example.ssumeet.fragment;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.ssumeet.FirebaseHelper;
import com.example.ssumeet.OnPostListener;
import com.example.ssumeet.PostInfo;
import com.example.ssumeet.R;
import com.example.ssumeet.ReadContentsView;
import com.example.ssumeet.WritePost;

import java.io.Serializable;

public class PostFragment extends Fragment {

    private PostInfo postInfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsVIew;
    private LinearLayout contentsLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.post_fragment, container, false);

        /*setHasOptionsMenu(true);

        postInfo = (PostInfo) getActivity().getIntent().getSerializableExtra("postInfo");
        contentsLayout = view.findViewById(R.id.contentsLayout);
        readContentsVIew = view.findViewById(R.id.readContentsView);

        //firebaseHelper = new FirebaseHelper(PostFragment.class);
        firebaseHelper.setOnPostListener(onPostListener);
        uiUpdate();*/

        return view;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    postInfo = (PostInfo)data.getSerializableExtra("postinfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }



@Override
    public boolean onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.post, menu);
        return super;    //super.onCreateOptionsMenu(menu);  수정필요
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                firebaseHelper.storageDelete(postInfo);
                return true;
            case R.id.modify:
                //myStartActivity(WritePost.class, postInfo);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete(PostInfo postInfo) {
            Log.e("로그 ","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그 ","수정 성공");
        }
    };

    private void uiUpdate(){
        readContentsVIew.setPostInfo(postInfo);
    }

private void myStartActivity(Class c, PostInfo postInfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("postInfo", (Serializable) postInfo);
        startActivityForResult(intent, 0);
    }*/

}
