window.SXMT=(function() {
    var SXMT = {};

    /** Foundation-ize the document **/
    $(document).foundation();

    /** Attach FastClick to the document body **/
    $(function() {FastClick.attach(document.body);});

    /** Handlebars Template Compilation **/
    var templates = {};
    (function() {
        var parent = document.getElementById("sxmtBody");
        // Make sure your templates are prefaced with "HT_"
        var compileTemplate = function(templateId, registerPartial, skipAddingTemplate) {
            var template = document.getElementById("HT_" + templateId);
            if (registerPartial) Handlebars.registerPartial(templateId, template.innerHTML);
            if (!skipAddingTemplate) templates[templateId] = Handlebars.compile(template.innerHTML);
            parent.removeChild(template);
        };
        /** Partials - Need to register these before templates in case they're referenced **/
        compileTemplate("videoUrl", true, true);
        /** Templates **/
        compileTemplate("videoHeader");
        compileTemplate("videoEmbedUrl");
        compileTemplate("stations");
    })();

    SXMT.info = {};

    /** Initialize Video Player **/
    SXMT.playerReady = false;
    var onPlayerReady = function(event) {
        $("#videoWrapper").addClass("noShow").removeClass("hideOffscreen");
//        event.target.playVideo();
        SXMT.playerReady = true;
    };
    var onPlayerStateChange = function(event) {
        if (SXMT.playerReady) {
            if (event.data == window.YT.PlayerState.PLAYING) {
                console.log("Video Playing");
                // TODO update status indicator
            } else if (event.data == window.YT.PlayerState.PAUSED) {
                console.log("Video Paused");
                // TODO update status indicator
            } else if (event.data == window.YT.PlayerState.ENDED) {
                console.log("Video Ended");
    //            SXMT.player.stopVideo();
                SXMT.loadNextSong(SXMT.info.currentStation, SXMT.info.currentSong.id, SXMT.info.currentSong.tweet);
            }
        }
    };
    window.onYouTubeIframeAPIReady = function() {
        SXMT.player = new window.YT.Player("videoPlayer", {
            height: "600",
            width: "100%",
            playerVars: {
                autohide: 1,
                iv_load_policy: 3,
                modestbranding: 1,
                color: "white",
                theme: "light"
            },
            events: {
                "onReady": onPlayerReady,
                "onStateChange": onPlayerStateChange
            }
        });
    };

    /** Load Station List **/
    SXMT.info.stations = [];
    SXMT.refreshStations = function() {
        $.ajax({
            url: "./rest/stations",
            contentType: "application/json;charset=utf-8"
        })
            .done(function(data) {
                console.log("Loaded stations", data);
                SXMT.info.stations = data;
                for (var i = 1; i < 5; i ++) {SXMT.info.stations[i] = SXMT.info.stations[0]}// TODO remove this. duplicating to test
                document.getElementById("stations").innerHTML = templates.stations(SXMT.info.stations);
                $("#stations").slick({
                    centerMode: true,
                    centerPadding: "60px",
                    slidesToShow: 3,
                    responsive: [
                        {
                            breakpoint: 768,
                            settings: {
                                arrows: false,
                                centerMode: true,
                                centerPadding: "40px",
                                slidesToShow: 3
                            }
                        },
                        {
                            breakpoint: 480,
                            settings: {
                                arrows: false,
                                centerMode: true,
                                centerPadding: "40px",
                                slidesToShow: 1
                            }
                        }
                    ]
                });
            })
            .fail(function(data) {
                alert("Could not load station list!");
            });
    };

    /** Load Next Song **/
    SXMT.loadNextSong = function(station, lastSong, lastTweet) {
        var data = JSON.stringify({
            station: station,
            lastSong: lastSong,
            lastTweet: lastTweet
        });
        $.ajax({
            url: "./rest/nextSong",
            method: "POST",
            contentType: "application/json",//;charset=utf-8",
            data: data
        })
            .done(function(data) {
                console.log("Loaded song", data);
                SXMT.info.currentSong = data;
                 $(document).trigger("loadVideo.sxmt");
             })
            .fail(function(data) {
                SXMT.info.currentStation = SXMT.info.lastStation;
                alert("Could not load station!");
            });
    };

    $(document).one("loadVideo.sxmt", function() {
        $("#videoHeader").removeClass("noMargin");
        $("#videoWrapper").removeClass("noShow");
    });
    $(document).on("loadVideo.sxmt", function() {
        SXMT.player.loadVideoById(SXMT.info.currentSong.id);
        document.getElementById("videoHeader").innerHTML = templates.videoHeader(SXMT.info.currentSong);
    });

    /** Initialize Station List **/
    $("#stations").on("click", ".station", function() {
        var $this = $(this);
        var tmp = $this.attr("data-station");
        if (SXMT.info.currentStation !== tmp) {
            SXMT.info.lastStation = SXMT.info.currentStation;
            SXMT.info.currentStation = tmp;
            SXMT.loadNextSong(SXMT.info.currentStation);
        }
    });
    SXMT.refreshStations();

    return SXMT;
})();