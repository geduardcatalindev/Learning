package paulscode.android.mupen64plus;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ListIterator;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

// TODO: Comment thoroughly
public class MenuSettingsVideoChangeActivity extends ListActivity implements IFileChooser
{
    public static MenuSettingsVideoChangeActivity mInstance = null;
    private OptionArrayAdapter optionArrayAdapter;  // array of menu options

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        mInstance = this;

        List<MenuOption>optionList = new ArrayList<MenuOption>();
        optionList.add( new MenuOption( "Import...", "add new video plug-in", "MenuSettingsVideoChangeImport" ) );

        Config video_list = new Config( Globals.DataDir + "/plug-ins/video_list.ini" );
        ListIterator<Config.ConfigSection> iter = video_list.listIterator();
        Config.ConfigSection section;
        while( iter.hasNext() )
        {   // Loop through the sections
            section = iter.next();
            if( !section.name.equals( "[<sectionless!>]" ) )
                optionList.add( new MenuOption( section.get( "name" ), section.get( "info" ), section.name ) );
        }
        optionArrayAdapter = new OptionArrayAdapter( this, R.layout.menu_option, optionList );
        setListAdapter( optionArrayAdapter );
    }

    public void fileChosen( String filename )
    {
        if( filename == null )
        {
            Log.e( "MenuSettingsVideoChangeActivity", "filename null in method fileChosen" );
            return;
        }
        File archive = new File( filename );
        String pluginName = archive.getName();
        if( pluginName == null )
        {
            Log.e( "MenuSettingsVideoChangeActivity", "plug-in name null in method fileChosen" );
            return;
        }
        pluginName = pluginName.substring( 0, pluginName.length() - 4 );

//        if( SDLActivity.unzipAll( archive, Globals.DataDir + "/plug-ins/" + pluginName ) )
//        {
            //TODO: finish implementing
//        }
    }

    /*
     * Determines what to do, based on what option the user chose 
     * @param listView Used by Android.
     * @param view Used by Android.
     * @param position Which item the user chose.
     * @param id Used by Android.
     */
    @Override
    protected void onListItemClick( ListView listView, View view, int position, long id )
    {
        super.onListItemClick( listView, view, position, id );
        MenuOption menuOption = optionArrayAdapter.getOption( position );
        if( menuOption.info.equals( "MenuSettingsVideoChangeImport" ) )
        {  // open the file menu to choose a plug-in
            String path = MenuActivity.gui_cfg.get( "LAST_SESSION", "so_folder" );

            if( path == null || path.length() < 1 )
                FileChooserActivity.startPath = Globals.StorageDir;
            else
                FileChooserActivity.startPath = path;
            FileChooserActivity.extensions = ".zip";
            FileChooserActivity.parentMenu = mInstance;
            FileChooserActivity.function = FileChooserActivity.FUNCTION_SO;
            Intent intent = new Intent( mInstance, FileChooserActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity( intent );
        }
        else
        {
            if(  MenuSettingsVideoActivity.mInstance != null )
                 MenuSettingsVideoActivity.mInstance.fileChosen( menuOption.info );
            mInstance.finish();
        }
    }
}
