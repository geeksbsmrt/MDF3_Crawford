$("#home").on('pageshow', function() {
    var type;
	$('fieldset').hide();
	$("#lookupType").change(function () {
	    var display = $("#lookupType option:selected").val();
        if (display === "charLookup") {
            type = "char";
            $("#guildLookup").hide();
            $("#charLookup").show();
        } else if (display === "guildLookup") {
            type = "guild";
            $("#charLookup").hide();
            $("#guildLookup").show();
        } else {
            //something is wrong
        }
	});
	$("#submitButton").on('click', function(e){
	    e.preventDefault();
        var realm = $("#realmName").val(),
            guild,
            char;
        if (type === "char") {
            char = $("#cName").val();
            Android.charLookup(realm, char);
        } else if (type === "guild") {
            guild = $("#gName").val();
            Android.guildLookup(realm, guild);
        } else {
            //something wrong
        }
	});
});