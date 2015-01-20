//This file is used for error summary page (error.html)

//Note:
//hide an item:
//style.display = "none"
//
//show an item:
//style.display = ""


//switch a item visible/invisible
function sw(itemId) {
    var tbody = document.getElementById('tbody_' + itemId);
    if (tbody.style.display == "none") {
        showItem(itemId);
    } else {
        hideItem(itemId);
    }
}

//show an item
function showItem(itemId) {
    var tbody = document.getElementById('tbody_' + itemId);
    tbody.style.display = "";
    var expandimage = document.getElementById('expand_' + itemId);
    expandimage.src = "img/nolines_minus.gif";
}

//hide an item
function hideItem(itemId) {
    var tbody = document.getElementById('tbody_' + itemId);
    tbody.style.display = "none";
    var expandimage = document.getElementById('expand_' + itemId);
    expandimage.src = "img/nolines_plus.gif";
}

//hide all items under this type
function hideType(typeId) {
    for (var j = 1; (document.getElementById('tbody_' + typeId + '_' + j)) != null; j++) {
        var tbody = document.getElementById('tbody_' + typeId + '_' + j);
        if (tbody) {
            if (tbody.style.display == "") {
                tbody.style.display = "none";
                var expandimage = document.getElementById('expand_' + typeId + '_' + j);
                expandimage.src = "img/nolines_plus.gif";
            }
        }
    }
}

//show all items under this type
function showType(typeId) {
    for (var j = 1; (document.getElementById('tbody_' + typeId + '_' + j)) != null; j++) {
        var tbody = document.getElementById('tbody_' + typeId + '_' + j);
        if (tbody) {
            if (tbody.style.display == "none") {
                tbody.style.display = "";
                var expandimage = document.getElementById('expand_' + typeId + '_' + j);
                expandimage.src = "img/nolines_minus.gif";
            }
        }
    }
}


//show or hide the type, this is used by checkbox
function hideShowType(type){
    if (type.checked){
        showType(type.name);
    } else {
        hideType(type.name);
    }
}
