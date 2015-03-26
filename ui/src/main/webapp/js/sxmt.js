window.SXMT=(function() {
    var SXMT = {};

    /** Foundation-ize the document **/
    $(document).foundation();

    /** Attach FastClick to the document body **/
    $(function() {FastClick.attach(document.body);});

    /** Resize Handler to Manage History Max-Height **/
    var resizeTimer,
        //largeScreenMQL = window.matchMedia("only screen and (min-width: 64.063em)"),
        setHistoryMaxHeight = function(mql) {
            console.log("mq change", mql);
            if (mql.matches) {
                $("#videoHistoryNav").css("max-height", $("#videoColumn").height() - $("#videoHistoryHeader").height() - parseInt($("#historyColumn").css("margin-bottom")));
            }/* else {
                $("#videoHistoryNav").css("max-height", "none");
            }*/
        };
    //largeScreenMQL.addListener(setHistoryMaxHeight);
    $(window).on("resize", function() {
        clearTimeout(resizeTimer);
        setTimeout(function() {
            setHistoryMaxHeight(window.matchMedia("only screen and (min-width: 64.063em)"));
        }, 100);
    });
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
        compileTemplate("historyEntry", true);
        compileTemplate("history");
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
                SXMT.loadSong(SXMT.info.currentStation, SXMT.info.currentSong.id, SXMT.info.currentSong.tweet);
            }
        }
    };
    var onPlayerError = function(event) {
        console.log("Error playing video.", event);
        SXMT.loadSong(SXMT.info.currentStation, SXMT.info.currentSong.id, SXMT.info.currentSong.tweet);
        // Remove song from history
        if(sessionStorage) {
            var history = sessionStorage.getItem("sxmt_history");
            if (history) {
                history = JSON.parse(history);
                history.shift();
            } else {
                history = [];
            }
            sessionStorage.setItem("sxmt_history", JSON.stringify(history));
        } else {
            // Will need to do something else...
            console.log("Session Storage is not available when attempting to remove song.");
        }
        var historyEntries = $("#videoHistory").find(">.historyEntry");
        if (historyEntries.length > 0) historyEntries.first().remove();
    };
    window.onYouTubeIframeAPIReady = function() {
        SXMT.player = new window.YT.Player("videoPlayer", {
            //height: "600",
            height: "70%",
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
                "onStateChange": onPlayerStateChange,
                "onError": onPlayerError
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
//                if (data.length < 1) {alert("No stations loaded!");return;}
//                for (var i = 1; i < 5; i ++) {SXMT.info.stations[i] = data[0]}// TODO remove this. duplicating to test
                document.getElementById("stations").innerHTML = templates.stations(data);
                $("#stations").slick({
                    centerMode: true,
                    centerPadding: "60px",
                    slidesToShow: 3,
                    focusOnSelect: true,
//                    autoplay: true,
//                    autoplaySpeed: 5000,
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

    /** Load Song **/
    SXMT.loadSong = function(station, song, tweet, ignoreReferenceTweet) {
        // TODO we might not want this actually, playing through the history isn't necessarily a bad thing... :/
        if (SXMT.info.referenceTweet && !ignoreReferenceTweet) {
            tweet = SXMT.info.referenceTweet;
            delete SXMT.info.referenceTweet;
        }
        var data = JSON.stringify({
            station: station,
            song: song,
            tweet: tweet,
            next: ignoreReferenceTweet ? 0 : 1
        });
        $.ajax({
            url: "./rest/song",
            method: "POST",
            contentType: "application/json",//;charset=utf-8",
            data: data
        })
            .done(function(data) {
                console.log("Loaded song", data);
                if (data.referenceTweet && !SXMT.info.referenceTweet) SXMT.info.referenceTweet = data.referenceTweet;
                // TODO may want to get station later as well in case loading songs from other stations
                SXMT.info.currentSong = data;
                if (data.tweet) SXMT.addSongToHistory(SXMT.info.currentStation, SXMT.info.currentSong);
                $(document).trigger("loadVideo.sxmt");
             })
            .fail(function(data) {
                SXMT.info.currentStation = SXMT.info.lastStation;
                alert("Could not load station!");
            });
    };

    /** Load History **/
    SXMT.refreshHistory = function() {
        if(sessionStorage) {
            var history = sessionStorage.getItem("sxmt_history");
            if (history) {
                document.getElementById("videoHistory").innerHTML = templates.history(JSON.parse(history));
            }
        } else {
            // Try loading from somewhere else...
            console.log("Session Storage is not available. Cannot refresh history.");
        }
    };

    var historyContainsEntry = function(entry, history) {
        var index = -1, entryTweet = entry.song.tweet;//entryJson = JSON.stringify(entry);
        for (var i = 0, l = history.length; i < l; i ++) {
            //if (entryJson === JSON.stringify(history[i])) {
            if (entryTweet === history[i].song.tweet) {
                index = i;
                break;
            }
        }
        return index;
    };

    /** Add Song to History **/
    SXMT.addSongToHistory = function(station, song) {
        if (!(station && song && song.tweet && song.id && song.song)) return;
        var MAX_HISTORY_ENTRIES = 10, existsAtIndex = -1,
            entry = {
                station: station,
                song: song
            };
        console.log("Adding to history", station, song);
        // Update session history
        if(sessionStorage) {
            var history = sessionStorage.getItem("sxmt_history");
            if (history) {
                history = JSON.parse(history);
                if ((existsAtIndex = historyContainsEntry(entry, history)) > -1) history.splice(existsAtIndex, 1);
                else if (history.length === MAX_HISTORY_ENTRIES) history.pop();
            } else {
                history = [];
            }
            history.unshift(entry);
            sessionStorage.setItem("sxmt_history", JSON.stringify(history));
        } else {
            // Will need to do something else...
            console.log("Session Storage is not available. Cannot save for session", station, song);
        }
        // Update UI history
        var videoHistory = $("#videoHistory"), historyEntries = videoHistory.find(">.historyEntry");
        /*if (existsAtIndex > -1) {
            var duplicate = historyEntries.get(existsAtIndex);
            if (duplicate) duplicate.remove();
        } else */if (historyEntries.length === MAX_HISTORY_ENTRIES) {
            historyEntries.last().remove();
        }
        videoHistory.prepend(templates.historyEntry(entry));
    };

    $(document).one("loadVideo.sxmt", function() {
        $("#videoHeader").removeClass("noMargin");
        $("#videoWrapper").removeClass("noShow");
        $("#videoSkip").show();
        setHistoryMaxHeight(window.matchMedia("only screen and (min-width: 64.063em)"));
        SXMT.refreshHistory();
    });
    $(document).on("loadVideo.sxmt", function() {
        document.getElementById("videoHeader").innerHTML = templates.videoHeader(SXMT.info.currentSong);
        SXMT.player.loadVideoById(SXMT.info.currentSong.id);
    });

    /** Set Station Backdrop **/
    var updateStationBackdrop = function(imgUrl) {
        $("sxmtBody").css("background-image", "url('" + imgUrl + "'),url('images/noImg.png')");
    };

    /** Initialize Station List **/
    $("#stations").on("click", ".station", function() {
        var $this = $(this);
        var tmp = $this.attr("data-station");
        if (SXMT.info.currentStation !== tmp) {
            SXMT.info.lastStation = SXMT.info.currentStation;
            SXMT.info.currentStation = tmp;
            SXMT.loadSong(SXMT.info.currentStation);
            updateStationBackdrop(SXMT.info.currentStation.backdrop);
            $("html, body").animate({scrollTop: 0}, "slow");
        }
    });
    SXMT.refreshStations();

    /** Initialize History **/
    $("#videoHistory").on("click", ".historyEntry", function(event) {
        event.preventDefault();
        var $this = $(this);
        // Added reference tweet id so we don't end up playing through the history since the video selected
        if (!SXMT.info.referenceTweet) SXMT.info.referenceTweet = SXMT.info.currentSong.tweet;
        var station = $this.attr("data-station"), song = $this.attr("data-song"), tweet = $this.attr("data-tweet");
        $this.remove();
        SXMT.loadSong(station, song, tweet, true);
        $("html, body").animate({scrollTop: 0}, "slow");
    });
    //SXMT.refreshHistory();

    /** Setup Next Video Button **/
    $("#videoSkip").on("click", function() {SXMT.loadSong(SXMT.info.currentStation, SXMT.info.currentSong.id, SXMT.info.currentSong.tweet);});

    return SXMT;
})();