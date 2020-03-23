package me.minitrabajo.model;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Scott on 17/10/2016.
 */
public class Conversations implements Serializable {

    private static final long serialVersionUID = 2553566873643403224L;
    public static final String CONVERSATIONS_FILE_NAME = "conversations.dat";
    //private transient Context context;
    private List<Conversation> conversations;

    public Conversations()
    {
       // this.context = context;
        this.conversations = new ArrayList<Conversation>(1);
    }

    public Conversations( Conversations conversations)
    {
        //this.context = context;
        this.conversations = new ArrayList<Conversation>(1);

        //Assume adding of new conversations and messages
        this.addAll(conversations);

    }

    protected Conversation getConversation(int i)
    {
        return conversations.get(i);
    }

    protected Conversation find(Conversation conversation)
    {
        Conversation result = null;

        if (conversation.getUsers().size() >= 2)
        {
            for (int i = 0; i < conversations.size(); i++)
            {
                result = conversations.get(i);

                if (result.equals(conversation))
                {
                    break;
                }
                else
                {
                    result = null;
                }
            }
        }
        return result;
    }

    public Conversation find(Users users)
    {
        Conversation conversation = null;
        if (users.size() >= 2)
        {
            for (int i = 0; i < conversations.size(); i++)
            {
                conversation = conversations.get(i);
                if (conversation.contains(users))
                {
                    return conversation;
                }
            }
        }

        return conversation;
    }

    /*public Conversation find(String toEmail, String fromEmail)
    {
        Conversation result = null;

        for (int i =0; i < conversations.size();i++)
        {
            result = conversations.get(i);

            if (result.contains(toEmail,fromEmail) )
            {
                break;
            }
            else
            {
                result = null;
            }
        }
        return result;
    }*/


    public Conversation find(String conversationId)
    {
        Conversation result = null;

        for (int i =0; i < conversations.size();i++)
        {
            result = conversations.get(i);

            if (result.getId().equals(conversationId))
            {
                break;
            }
            else
            {
                result = null;
            }
        }
        return result;
    }

    public int size()
    {
        return conversations.size();
    }

    public void add(Conversation c)
    {
        //Check for duplication
        if (conversations.contains(c))
        {
            //don't add conversation as already exists
            Log.v("Conversations","attempt to add duplicate conversation");
        }
        else
        {
            conversations.add(c);
        }
    }

   /* public void addAll(Conversations conversations)
    {
        //Used to add new messages to current conversations
        for (int i =0; i < conversations.size();i++)
        {
            Conversation conversation = conversations.getConversation(i);
            if (!this.contains(conversation))
            {
                //Conversation is new so just add it
                this.add(conversation);
            }
            else
            {
                //Find conversation and add messages
                Messages messages = conversation.getMessages();
                this.find(conversation).getMessages().add(messages);
            }

        }
    }*/

    public void addAll(Conversations that)
    {
        //Used to add new messages to current conversations
        //Check for new conversations
        for (int i =0; i < that.size() ;i++)
        {
            if(!this.contains(that.getConversation(i)))
            {
                //No conversation found so add entire new conversation
                this.add(that.getConversation(i));
            }
            else
            {
                //Just add new messages to existing conversation as we know they are new
                this.find(that.getConversation(i)).getMessages().add(that.getConversation(i).getMessages());
            }
        }
    }

   /* protected void add(Message c)
    {
        //Could check for duplication
        this.find(c.getTo(),c.getFrom()).getMessages().add(c);
    }*/

    protected boolean contains(Conversation conversation)
    {
        //Only compares id or users
        boolean result = false;
        for (int i =0; i < conversations.size();i++)
        {
            Users users = conversations.get(i).getUsers();
            if (conversations.get(i).getId().equals(conversation.getId()))
            {
                //ID's match immediately return true result
                result = true;
                break;
            }
            else if (users.size() >= 2  )
            {
                //All users found in set
                if(users.contains(conversation.getUsers()))
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public List<Conversation> getConversationList()
    {
        return conversations;
    }

    protected void setConversationList(List<Conversation> conversation)
    {
        this.conversations = conversation;
    }

    public boolean isEmpty()
    {
        return this.size() == 0 ? true : false;
    }

    /*public String[] getConversationStringArray()
    {
        String[] names = new String[conversations.size()];
        for (int i = 0; i < conversations.size();i++ )
        {
            names[i] = conversations.get(i);
        }

        return names;
    }*/

    public void sort()
    {
        Collections.sort(conversations, new Comparator<Conversation>()
        {
            @Override
            public int compare(Conversation conversation1, Conversation conversation2)
            {
                return  conversation1.getId().compareTo(conversation2.getId());
            }
        });
    }

    public boolean hasFile(Context context)
    {
        boolean result = false;
        try
        {
            File file = context.getFileStreamPath(CONVERSATIONS_FILE_NAME);
            if(file == null || !file.exists()) {
                result= false;
            }
            else
            {
                result = true;
            }
        }
        catch (Exception ioex)
        {
            result = false;
            Log.v("Conversations:hasFile",ioex.getMessage());
        }
        finally
        {
            return result;
        }
    }

    /*public void deleteFile()
    {
        context.deleteFile(CONVERSATIONS_FILE_NAME);
    }*/


    public Conversation findConversationByMessage(String text)
    {
        Conversation result = null;

        for (int i =0; i < conversations.size();i++)
        {
            Messages messages = conversations.get(i).getMessages();
            for (int j =0; j < messages.size();j++)
            {
                if (messages.getMessage(i).getText().equals(text))
                {
                    result = conversations.get(i);
                    break;
                }
            }
        }
        return result;
    }

    /*
    *   Save Functions
    * */

    public void saveToFile(Context context)
    {
        try
        {
            FileOutputStream fos = context.openFileOutput(CONVERSATIONS_FILE_NAME , Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        }
        catch (Exception e)
        {
            Log.v("Conversations:Save",e.getMessage());
        }
    }

    public String saveToString()
    {
        String output = "";
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            output = Base64.encodeToString(baos.toByteArray(),0);
        }
        catch (NotSerializableException nosex)
        {
            System.out.print(nosex.getMessage());
            System.out.print(nosex.getStackTrace().toString());
            Log.v("Conversations:saveToStr", nosex.getMessage());
        }
        catch (Exception ex)
        {
            Log.v("Conversations:saveToStr", ex.getMessage());
        }
        return output;
    }

     /*
    *   Load Functions
    * */

    public void loadFromFile(Context context)
    {
        try
        {
            //Read file
            FileInputStream fis = context.openFileInput(CONVERSATIONS_FILE_NAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Conversations c = (Conversations) is.readObject();
            is.close();
            fis.close();

            this.setConversationList(c.getConversationList());

            Log.v("Conversations:loadFromF","Load from file success");
        }
        catch (Exception e)
        {
            Log.v("Conversations:loadFromF",e.getMessage());
        }
    }

    public void loadFromString( String str )
    {
        try
        {
            byte [] data = Base64.decode( str,0 );
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ) );
            Conversations c  = (Conversations)ois.readObject();
            ois.close();

            this.setConversationList(c.getConversationList());
        }
        catch (Exception ex)
        {
            Log.v("Conversations:deserial", ex.getMessage());
        }
    }

    public void loadFromJSON(String json, String deviceEmail)
    {
        //TODO: Need user info on first message to make this work both ways
        try
        {
            if (json.length() < 20)
            {
                if (json.substring(0,19).contains("read"))
                {
                    JSONArray messagesJSON = new JSONObject(json).getJSONArray("read");
                    Messages messages = new Messages();
                    for (int i = 0; i < messagesJSON.length(); i++) {
                        JSONObject messageJSON = messagesJSON.getJSONObject(i);
                        Message message = new Message(messageJSON.getString("to"),messageJSON.getString("from"),messageJSON.getString("text"), messageJSON.getLong("timestamp") );
                        messages.add(message);
                    }

                   this.addAll(messages.sortByConversations(deviceEmail));

                    Log.v("Conversations:JSON:OK", String.valueOf(this.size()));
                }
                else if (json.substring(0,19).contains("readalllasts"))
                {
                    JSONArray messagesJSON = new JSONObject(json).getJSONArray("readalllasts");
                    Messages messages = new Messages();
                    for (int i = 0; i < messagesJSON.length(); i++) {
                        JSONObject messageJSON = messagesJSON.getJSONObject(i);
                        Message message = new Message(messageJSON.getString("to"),messageJSON.getString("from"),messageJSON.getString("text"), messageJSON.getLong("timestamp") );
                        messages.add(message);
                    }
                    this.addAll(messages.sortByConversations(deviceEmail));
                    Log.v("Conversations:JSON:OK", String.valueOf(this.size()));
                }
                else
                {
                    Log.v("Conversations:JSON:Fail", String.valueOf(this.size()));
                }
            }

        }
        catch (Exception ex)
        {
            Log.v("Conversations:JSON:ERR", ex.getMessage());
        }
    }

    public void print()
    {
        try{
            Log.v("Conversations", "Object");
            Log.v("Conversations Size", String.valueOf(this.size()));
            for(int i = 0; i < conversations.size(); i++)
            {
                Log.v("Messages" ,  String.valueOf( conversations.get(i).getMessages().size()));
                Log.v("users" ,  String.valueOf( conversations.get(i).getUsers().size()));
                Users users = conversations.get(i).getUsers();
                if (i==3){break;}
                for(int j = 0; j < users.size(); j++)
                {
                    Log.v("User" ,  users.getUser(j).getName());
                    if (j==3){break;}
                }

                Messages messages = conversations.get(i).getMessages();
                if (i==3){break;}
                for(int j = 0; j < messages.size(); j++)
                {
                    Log.v("Message" ,  messages.getMessage(j).getText());
                    if (j==3){break;}
                }

            }
        } catch (Exception ex){Log.v("Conversations:Err:print", ex.getMessage());}

    }
}
