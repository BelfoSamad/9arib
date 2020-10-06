package com.belfoapps.qarib.ui.custom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.databinding.ContactsDialogBinding;
import com.belfoapps.qarib.databinding.SocialMediaDialogBinding;
import com.belfoapps.qarib.databinding.WebsiteDialogBinding;
import com.belfoapps.qarib.pojo.Post;
import com.belfoapps.qarib.utils.Types;

public class CreatePostDialog extends DialogFragment {
    private static final String TAG = "PostDialog";

    public interface PostCreationListener {

        void createPost(Post post);

    }

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private PostCreationListener listener;
    private String type;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    public CreatePostDialog(String type) {
        this.type = type;
    }

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            listener = (PostCreationListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement Callback interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        Dialog dialog = null;

        switch (Types.valueOf(type.replaceAll("\\s+", ""))) {
            case Contacts:
                dialog = getContactsDialog(builder, inflater);
                break;
            case SocialMedia:
                dialog = getSocialMediaDialog(builder, inflater);
                break;
            case Website:
                dialog = getWebsiteDialog(builder, inflater);
                break;
            case Location:
                Log.d(TAG, "onCreateDialog: Location thing");
                break;
        }

        //Make Transparent Background
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    private Dialog getContactsDialog(AlertDialog.Builder builder, LayoutInflater inflater) {

        //Set ViewBinding
        ContactsDialogBinding cBinding = ContactsDialogBinding.inflate(inflater, null,
                false);

        //Create Post
        cBinding.createPost.setOnClickListener(v -> {
            if (cBinding.contactsTitle.getEditText().getText().toString().equals(""))
                cBinding.contactsTitle.setError(getResources().getString(R.string.title_error));
            else if (cBinding.contactsContent.getEditText().getText().toString().equals(""))
                cBinding.contactsContent.setError(getResources().getString(R.string.content_error));
            else if (cBinding.contactsEmail.getEditText().getText().toString().equals(""))
                cBinding.contactsEmail.setError(getResources().getString(R.string.email_error));
            else if (cBinding.contactsPhone.getEditText().getText().toString().equals(""))
                cBinding.contactsPhone.setError(getResources().getString(R.string.phone_error));
            else {
                Post post = new Post(null, cBinding.contactsTitle.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setContent(cBinding.contactsContent.getEditText().getText().toString());
                post.setEmail(cBinding.contactsEmail.getEditText().getText().toString());
                post.setPhone(cBinding.contactsPhone.getEditText().getText().toString());

                listener.createPost(post);
            }
        });

        //Close Listener
        cBinding.close.setOnClickListener(v -> dismiss());

        return builder.setView(cBinding.getRoot()).create();
    }

    private Dialog getSocialMediaDialog(AlertDialog.Builder builder, LayoutInflater inflater) {

        //Set ViewBinding
        SocialMediaDialogBinding smBinding = SocialMediaDialogBinding.inflate(inflater, null,
                false);

        //Create Post
        smBinding.createPost.setOnClickListener(v -> {
            if (smBinding.socialMediaTitle.getEditText().getText().toString().equals(""))
                smBinding.socialMediaTitle.setError(getResources().getString(R.string.title_error));
            else if (smBinding.socialMediaContent.getEditText().getText().toString().equals(""))
                smBinding.socialMediaContent.setError(getResources().getString(R.string.content_error));
            else {
                Post post = new Post(null, smBinding.socialMediaTitle.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setTitle(smBinding.socialMediaContent.getEditText().getText().toString());

                listener.createPost(post);
            }

        });

        //Close Listener
        smBinding.close.setOnClickListener(v -> dismiss());

        return builder.setView(smBinding.getRoot()).create();
    }

    private Dialog getWebsiteDialog(AlertDialog.Builder builder, LayoutInflater inflater) {

        //Set ViewBinding
        WebsiteDialogBinding wBinding = WebsiteDialogBinding.inflate(inflater, null,
                false);

        //Create Post
        wBinding.createPost.setOnClickListener(v -> {
            if (wBinding.websiteTitle.getEditText().getText().toString().equals(""))
                wBinding.websiteTitle.setError(getResources().getString(R.string.title_error));
            else if (wBinding.websiteContent.getEditText().getText().toString().equals(""))
                wBinding.websiteContent.setError(getResources().getString(R.string.content_error));
            else if (wBinding.website.getEditText().getText().toString().equals(""))
                wBinding.website.setError(getResources().getString(R.string.website_error));
            else {
                Post post = new Post(null, wBinding.websiteTitle.getEditText().getText().toString(),
                        System.currentTimeMillis());
                post.setTitle(wBinding.websiteContent.getEditText().getText().toString());
                post.setWebsite(wBinding.website.getEditText().getText().toString());

                listener.createPost(post);
            }
        });

        //Close Listener
        wBinding.close.setOnClickListener(v -> dismiss());

        return builder.setView(wBinding.getRoot()).create();
    }
}
