/***
WIPS, a WiFi-Based Indoor Positioning System
Copyright (c) 2008 - 2009, University of Toronto
scyp@atrc.utoronto.ca
http://scyp.atrc.utoronto.ca/wips/

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program, see enclosed file gpl-3.txt or browse to
<http://www.gnu.org/licenses/gpl.txt>

@author: Jorge Silva, Susahosh Rahman
***/

//(function ($, fluid) {

    //var uiOptionsNode;

    //initialize the UI Enhancer
//    var initUIEnhancer = function () {
    
//        var enhancerOpts = {
            //defaultSiteSettings: {
//                theme: "mist",
                //linksBold: true,
                //linksUnderline: true
            //},
/*            tableOfContents: {
                options: {
                    templateUrl: "infusion/components/tableOfContents/html/TableOfContents.html"
                }
            }*/
        //};
        
        //return fluid.uiEnhancer(document, enhancerOpts);
    //};
    
//     //load the UI Options component
//     var loadUIOptions = function () {
//         var urlSelector = "infusion/components/uiOptions/html/UIOptions.html .uiOptions";
//         uiOptionsNode.load(urlSelector, initUIOptions);
//     };
//     
//     var setupDialog = function() {
//     
//         // Create the dialog
//         uiOptionsNode.dialog({
//             bgiframe: true,
//             width: '60em',
//             modal: true,
//             closeOnEscape: false,
//             autoOpen: false,
//             draggable: true
//         });
//         
//         // Bind event handlers for it.
//         $("#uiOptions-button").click(function() {
//             uiOptionsNode.dialog("open");
//         });
//     };
//     
//         //initialize UI Options component
//     var initUIOptions = function() {
//         var options = {
//             listeners: {
//                 afterRender: function() {
//                     $(".uiOptions .fl-col:eq(0)").accordion({
//                         header: 'h2',
//                         clearStyle: true,
//                         autoHeight: false
//                     });
//                     $(".uiOptions .fl-col h2:eq(0)").focus();
//                 },
//                 onCancel: function() {
//                     uiOptionsNode.dialog("close");
//                 },
//                 onSave: function() {
//                     uiOptionsNode.dialog("close");
//                 }
//             }
//         };
// 
//         return fluid.uiOptions(".uiOptions", options);
//     };


    //as soon as web document is loaded...
    $(document).ready(function () {

        //Fluid Components
        //uiOptionsNode = $("#myUIOptions"); //selector in which to load ui options

        //initUIEnhancer();
        //loadUIOptions();
        //setupDialog();	

        //Tabs
        AddressTab = $("#specsTab-address");
        IndoorTab = $("#specsTab-indoor");

        AddressTab.click( function() {
                IndoorTab.removeClass("fl-tabs-active");
                AddressTab.addClass("fl-tabs-active");
                $("#indoorTab-content").css("display","none");
                $("#addressTab-content").css("display","block");
        });
        IndoorTab.click( function() {
                AddressTab.removeClass("fl-tabs-active");
                IndoorTab.addClass("fl-tabs-active");
                $("#addressTab-content").css("display","none");
                $("#indoorTab-content").css("display","block");
        });
    });

//})(jQuery, fluid);
