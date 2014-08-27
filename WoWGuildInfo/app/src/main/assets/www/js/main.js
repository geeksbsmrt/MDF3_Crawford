
$(document).on('pagebeforeshow', "#home", function () {
$('#realmName').append(new Option('Foo', 'foo', true, true));

});

$("#home").on('pageshow', function() {
$('fieldset').hide();

});