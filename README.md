A TeamCity plugin allowing you to customize TeamCity server pages by including some custom content or serving static pages separately.

Requirements:
=============
TeamCity 2017.1 or newer

License:
========
Apache 2.0

Builds:
=======
Download the plugin from [teamcity.jetbrains.com]( https://teamcity.jetbrains.com/viewType.html?buildTypeId=TeamCityPluginsByJetBrains_Unsorted_StaticUiExtensionsAgainstTeamCity90x)

To build locally, open the `build.gradle` project and run `serverPlugin` task. Check [gradle-teamcity-plugin](https://github.com/rodm/gradle-teamcity-plugin) 
for more development documentation

Usage:
======

1) To include custom content as a part of server pages:

a. Locate the plugin configuration file under:

    [TeamCity Data Directory]/config/_static_ui_extensions/static-ui-extensions.xml


In the configuration file, specify the place on the page and the static file to be included.
HTML, css or js resources are supported. 


Here is a sample rule:
```xml
     <rule place-id="[put place id here]" 
           html-file="[include html file from this folder]"
           js-file="[include js file from this folder]"
           css-files="[include css file from this folder]">
         
         <!-- this is the rule to make page place work only for URLs that starts with -->
         <url starts="overview.html" />

         <!-- use empty path to match all pages -->
         <url starts="" />

         <!-- this is the rule to only include content if URL is equal to  -->
         <url equals="viewType.html" />

        You can add as many rules / url constraints as you like.
     </rule>
```

The full list of page extensions is available at:
http://javadoc.jetbrains.net/teamcity/openapi/current/jetbrains/buildServer/web/openapi/PlaceId.html

The full list of the supported page places can be found in the page-places-list.txt file generated automatically on the server start.


Sample usages:
==============
 - add google analytics to a TeamCity installation
 - add instance-specific info/announcement
 - patch TeamCity CSS/JS
 - add static html widgets, e.g. Top Investigations, Build Status etc.


2) To include static pages served separately

Place the content you want to serve into the

[TeamCity Data Directory]/config/_static_ui_extensions/pages directory.

The pages will be available under the /<TC context path>/app/static_content/
