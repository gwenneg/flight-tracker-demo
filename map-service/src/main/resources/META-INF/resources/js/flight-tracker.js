


let loadAirports = () => {
    $.ajax({
        dataType: "json",
        url: "/flight",
        success: function(data) {
            data.forEach(airport => {
                $('#departure').append($('<option>').text(airport.code));
                $('#arrival').append($('<option>').text(airport.code));
            });
            $('#departure').val('BOD');
            $('#arrival').val('CDG');
        },
        statusCode: {
            404: () => {
                $('#error').text("Degraded mode: the server is unavailable (404), using hardcoded airports").show();
                ['BOD', 'CDG', 'SXB', 'TLS'].forEach(airport => {
                    $('#departure').append($('<option>').text(airport));
                    $('#arrival').append($('<option>').text(airport));
                });
                $('#departure').val('BOD');
                $('#arrival').val('CDG');
            }
        }
    });
};






$(document).ready(() => {
    connect();
    loadAirports();
});
$('#show-log').change(() => {
    $('#log').toggle();
});
$('#show-form').change(() => {
    $('#form').toggle();
});
let connect = () => {
    let socket = new WebSocket("ws://" + location.host + "/positions");

    socket.onopen = () => {
        console.log('Connected to the WebSocket');
    };

    socket.onmessage = function(message) {
        $('#log').append($('<li>').text(message.data));
        $('#log').scrollTop($('#log')[0].scrollHeight)
        let position = JSON.parse(message.data);

        let aircraft = $('#' + position.aircraft);
        if (position.landed) {
            aircraft.fadeOut(400, () => aircraft.remove());
        } else {

            if (!aircraft.length) {
                console.error("creating aircraft" + position.aircraft);
                // It is added to the map.
                aircraft = $('#aircraft-pattern')
                    .clone()
                    .attr('id', position.aircraft)
                aircraft.children('div').text(position.aircraft);
                $('#map').append(aircraft);
            }

            // Its position on the map is updated.
            aircraft.css({
                left: position.x - 20, // Half of the image width
                top: position.y - 20 // Half of the image height
            });
            aircraft.children('img').css({
                transform: 'rotate(' + (position.trackAngle - 90) + 'deg)'
            })
            aircraft.show();
        }
    };
};
$('#takeoff').click(() => {
    $.ajax({
        url: '/flight',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            aircraft: $('#aircraft').val(),
            departure: $('#departure').val(),
            arrival: $('#arrival').val(),
            speed: $('#speed').val(),
            source: $('#data-source').val()
        })
    })
});
$('#randomize').click(() => {
    for (i = 0; i < 20; i++) {
        $('#aircraft').val(makeid(1) + '-' + makeid(5));
        test($('#speed'));
        test($('#data-source'));
        test($('#departure'));
        do {
            test($('#arrival'));
        } while ($('#departure').val() == $('#arrival').val());
        $('#takeoff').click();
    }
});

let test = function(select) {
    let options = select.children('option');
    let random = Math.floor(options.length * Math.random());
    options.attr('selected', false).eq(random).attr('selected', true);
}

function makeid(length) {
    var result           = '';
    var characters       = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    var charactersLength = characters.length;
    for ( var i = 0; i < length; i++ ) {
        result += characters.charAt(Math.floor(Math.random() *
            charactersLength));
    }
    return result;
}


