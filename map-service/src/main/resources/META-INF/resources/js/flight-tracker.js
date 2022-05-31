let loadAirports = () => {
    $.ajax({
        url: "/frontend/airports",
        dataType: "json",
        success: function(data) {
            data.forEach(airport => {
                $('#departure').append($('<option>').text(airport.code));
                $('#arrival').append($('<option>').text(airport.code));
            });
            $('#departure').val('TLS');
            $('#arrival').val('CDG');
        },
        statusCode: {
            404: () => {
                $('#error').text("Degraded mode: the server is unavailable (404), using hardcoded airports").show();
                ['BOD', 'CDG', 'SXB', 'TLS'].forEach(airport => {
                    $('#departure').append($('<option>').text(airport));
                    $('#arrival').append($('<option>').text(airport));
                });
                $('#departure').val('TLS');
                $('#arrival').val('CDG');
            }
        }
    });
};

let connect = () => {
    let socket = new WebSocket("ws://" + location.host + "/broadcast-service");

    socket.onopen = () => {
        console.log('Connected to the WebSocket');
    };

    socket.onmessage = function(message) {
        let flightData = JSON.parse(message.data);

        let logUl = $('#log');
        logUl.append($('<li>').text(message.data));
        logUl.scrollTop(logUl[0].scrollHeight)

        let aircraftDiv = $('#' + flightData.aircraft);
        if (flightData.landed) {
            aircraftDiv.fadeOut(400, () => aircraftDiv.remove());
        } else {
            let isNew = false;

            if (!aircraftDiv.length) {
                isNew = true;
                aircraftDiv = $('#aircraft-pattern')
                    .clone()
                    .attr('id', flightData.aircraft)
                aircraftDiv.children('div').text(flightData.aircraft);
                $('#map').append(aircraftDiv);
            }

            if (isNew) {
                aircraftDiv.css({
                    left: flightData.x - 20,
                    top: flightData.y - 20
                });
                aircraftDiv.fadeIn();
            } else {
                aircraftDiv.animate({
                    left: flightData.x - 20,
                    top: flightData.y - 20
                }, 1000);
            }
            aircraftDiv.children('img').css({
                transform: 'rotate(' + (flightData.trackAngle - 90) + 'deg)'
            })
        }
    };
};

let selectRandomOption = function(select) {
    let options = select.children('option');
    let random = Math.floor(options.length * Math.random());
    options.attr('selected', false).eq(random).attr('selected', true);
};

let generateRandomString = function(length) {
    const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
    let result = '';
    for (let i = 0; i < length; i++) {
        result += characters.charAt(Math.floor(Math.random() * characters.length));
    }
    return result;
};

$(document).ready(() => {

    loadAirports();
    connect();

    $('#show-log').change(() => {
        $('#log').toggle();
    });

    $('#show-form').change(() => {
        $('#form').toggle();
    });

    $('#takeoff').click(() => {
        $.ajax({
            url: '/frontend/simulate',
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
        for (let i = 0; i < 20; i++) {
            $('#aircraft').val(generateRandomString(1) + '-' + generateRandomString(4));
            selectRandomOption($('#speed'));
            selectRandomOption($('#data-source'));
            selectRandomOption($('#departure'));
            do {
                selectRandomOption($('#arrival'));
            } while ($('#departure').val() == $('#arrival').val());
            $('#takeoff').click();
        }
    });

});
