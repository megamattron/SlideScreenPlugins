SlideScreen Plugin API
=============

This is the home of the [SlideScreen](http://slidescreenhome.com) Plugin API and examples. It is currently a "Developer Preview" 
and requires SlideScreen 2.0 Beta for integrated testing.

### Basic Structure

You are only required to provide two classes:

* A class that extends PluginReceiver and implements the abstract methods defined.
* A ContentProvider that responds to a specific set of fields.

Basically, your PluginReceiver responds to an Intent broadcast and returns the necessary info about your plugin, after which SlideScreen will query your ContentProvider for the actual data.

### The PluginReceiver

Here is the information SlideScreen needs to integrate your plugin, each type corresponds to a method in the receiver:

* A URI that points to your ContentProvider.
* A human readable name for your group.
* A default color to be shown beside the group in the interface.
* An SVG icon for the right side of the group in a format specified later.
* An array of Intents to try when the user single presses your icon. The Intents are tried in order until one works.
* An array of Intents for the long press of your icon.
* An Intent to launch your plugin's preferences activity.

There is also one callback method:

* markedAsRead(itemId) which you implement to do something when the user has marked one of your items as read.

### The ContentProvider

The best way to understand the implementation of the ContentProvider is to look at the basic example included in pluginbase/. After that, a more complicated example is included in the Google Voice plugin. In general:

* You are not responsible for storing your data, SlideScreen will do that for you. Just return the full set of data your plugin would wish to see on the home screen and we'll handle the rest. Any entries we don't see any more we'll assume have been read elsewhere, or no longer exist and will be removed from our interface.
* You are not responsible for scheduling your own updates, we will update on the schedule the user requests.
* You can just fail and return null if anything unexpected happens, including no network connection. We will retry your plugin later.
* If you detect a failure caused by incomplete or incorrect settings in your plugin, you can request that SlideScreen notify the user on the homescreen and direct them to your settings activity. See the Google Voice plugin for an example of this.
* You can request SlideScreen to refresh from your ContentProvider by notifying observers of a data change. In this way you can create a plugin that pushes changes to SlideScreen in realtime. (Note: This is relatively untested right now and might change in future API versions.)

Your plugin is limited in some fairly generous ways at the moment, we might tighten these if misbehaving plugins end up causing performance problems:

* Your total runtime cannot exceed 10 minutes. This really is just a safety limit, if you're taking anywhere close to this you have a problem. Most of our plugins can complete in under 30 seconds (on wifi).
* Your plugin cannot 'push' SlideScreen updates too often. How we throttle this is a little complicated, but just don't try to update a bunch of times in a row and you'll be ok.

### Misc

* Since our icon style is so simple we support a vector SVG icon file that will we scale for any needed size. There is an Illustrator icon template in the icontemplate folder that should help you format the file correctly. Save your results as TinySVG into the res/raw folder, see the examples for how that should look. Note: Please try not to break our hearts by using an insane icon.
* The Google Voice plugin included needs more work, especially in parsing message content. It would also be nice to be able to return calls from the interface and better integrate with the official Google Voice app.

Support
-------

Please use [our API specific support site](http://getsatisfaction.com/larvalabs/products/larvalabs_slidescreen_plugin_api) for support and suggestions.

Credits
-------

The Google Voice plugin makes use of modified copy of the [google-voice-java](http://code.google.com/p/google-voice-java/) library. We're trying to figure out the best way to contribute those changes back to the main project, but for the meantime the source is included here for ease of development.
