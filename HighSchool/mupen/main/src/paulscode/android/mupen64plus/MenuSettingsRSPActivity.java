package paulscode.android.mupen64plus;

import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

// TODO: Comment thoroughly
public class MenuSettingsRSPActivity extends ListActivity implements IFileChooser
{
    public static MenuSettingsRSPActivity mInstance = null;
    private OptionArrayAdapter optionArrayAdapter;  // array of menu options
    public static String currentPlugin = "(none)";
    public static boolean enabled = true;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        mInstance = this;

        currentPlugin = "(none)";

        String filename = MenuActivity.mupen64plus_cfg.get( "UI-Console", "RspPlugin" );
        if( filename == null || filename.length() < 1 || filename.equals( "\"\"" ) || filename.equals( "\"dummy\"" ) )
            filename = MenuActivity.gui_cfg.get( "RSP_PLUGIN", "last_choice" );
        if( filename != null )
        {
            MenuActivity.gui_cfg.put( "RSP_PLUGIN", "last_choice", filename );
            filename = filename.replace( "\"", "" );
            int x = filename.lastIndexOf( "/" );
            if( x > -1 && x < (filename.length() - 1) )
            {
                currentPlugin = filename.substring( x + 1, filename.length() );
                if( currentPlugin == null || currentPlugin.length() < 1 )
                    currentPlugin = "(none)";
            }
        }
        String en = MenuActivity.gui_cfg.get( "RSP_PLUGIN", "enabled" );
        if( en != null )
            enabled = en.equals( "1" ) ? true : false;

        List<MenuOption>optionList = new ArrayList<MenuOption>();
        optionList.add( new MenuOption( "Change Plug-in", currentPlugin, "menuSettingsRSPChange" ) );
        optionList.add( new MenuOption( "Enable", "use this plug-in", "menuSettingsRSPEnabled", enabled ) );

        optionArrayAdapter = new OptionArrayAdapter( this, R.layout.menu_option, optionList );
        setListAdapter( optionArrayAdapter );
    }
    public void fileChosen( String filename )
    {
        currentPlugin = "(none)";

        if( filename != null )
        {
            MenuActivity.gui_cfg.put( "RSP_PLUGIN", "last_choice", "\"" + filename + "\"" );
            MenuActivity.mupen64plus_cfg.put( "UI-Console", "RspPlugin", "\"" + filename + "\"" );
            int x = filename.lastIndexOf( "/" );
            if( x > -1 && x < ( filename.length() - 1 ) )
            {
                currentPlugin = filename.substring( x + 1, filename.length() );
                if( currentPlugin == null || currentPlugin.length() < 1 )
                    currentPlugin = "(none)";
            }
            else
                currentPlugin = filename;
        }

        optionArrayAdapter.remove( optionArrayAdapter.getItem( 0 ) );
        optionArrayAdapter.insert( new MenuOption( "Change", currentPlugin, "menuSettingsRSPChange" ), 0 );
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
        if( menuOption.info.equals( "menuSettingsRSPChange" ) )
        {  // open the menu to choose a plug-in
            Intent intent = new Intent( mInstance, MenuSettingsRSPChangeActivity.class );
            intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            startActivity( intent );
        }
        else if( menuOption.info.equals( "menuSettingsRSPEnabled" ) ) 
        {
            enabled = !enabled;
            optionArrayAdapter.remove( menuOption );
            optionArrayAdapter.add( new MenuOption( "Enable", "use this plug-in", "menuSettingsRSPEnabled",
                                                    enabled ) );
            MenuActivity.gui_cfg.put( "RSP_PLUGIN", "enabled", (enabled ? "1" : "0") );
            MenuActivity.mupen64plus_cfg.put( "UI-Console", "RspPlugin",
                (enabled ? MenuActivity.gui_cfg.get( "RSP_PLUGIN", "last_choice" ) : "\"dummy\"") );
        }
    }
}
