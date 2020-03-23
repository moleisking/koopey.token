package me.minitrabajo.model;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Scott on 17/10/2016.
 */
public class Conversation implements Serializable {

    private static final long serialVersionUID = 8653577983644503229L;
    private static String CONVERSATION_FILE_NAME = "conversation.dat";
    //private transient Context context;

    private String id = UUID.randomUUID().toString();
    private Messages messages;
    private Users users;

    public Conversation ()
    {
        this.users = new Users(users);
        this.messages = new Messages();
    }

    public Conversation ( Users users, Messages messages)
    {
        this.messages = messages;
        this.users = users;
    }

    public Conversation ( Users users)
    {
        this.users = new Users(users);
        this.messages = new Messages();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public Messages getMessages()
    {
        return messages;
    }

    public void setMessages(Messages messages)
    {
        this.messages = messages;
    }

    public Users getUsers()
    {
        return users;
    }

    public void setUsers(Users users)
    {
        this.users = users;
    }

    public boolean isEmpty()
    {
        return this != null && !this.users.equals("") ? true : false;
    }

    public boolean contains(Users users)
    {
        //user name and email need to be equal to return true
        boolean result = false;
        int counter = 0;
        for (int i =0; i < users.size();i++)
        {
            if (users.equals(users.getUser(i)))
            {
                counter++;
                if (counter == users.size())
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

   /* public boolean contains(String toEmail,String fromEmail)
    {
        boolean result = false;
        int counter = 0;
        for (int i =0; i < users.size();i++)
        {
            if (users.getUser(i).getEmail().equals(toEmail) || users.getUser(i).getEmail().equals(fromEmail))
            {
                counter++;
                if (counter == 2)
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }*/

    public boolean isEqual(Conversation conversation)
    {
        //Compares only users and not messages
        if(this.contains(conversation.users))
        {
            return  true;
        }
        else
        {
            return false;
        }
    }

    public void print()
    {
        try{
            Log.v("Conversation", "Object");
            Log.v("Id", this.getId());
            Log.v("User Size", String.valueOf(this.users.size()));
            Log.v("Message Size", String.valueOf(this.messages.size()));
            for(int i = 0; i < users.size(); i++)
            {
                Log.v("User" ,  users.getUser(i).getName() +":"+ users.getUser(i).getEmail());
                if (i==3){break;}
            }
            for(int i = 0; i < messages.size(); i++)
            {
                Log.v("Message" ,  messages.getMessage(i).getTo() +":"+ messages.getMessage(i).getFrom());
                if (i==3){break;}
            }
        } catch (Exception ex){Log.v("Conversation:err:print", ex.getMessage());}
    }

}
