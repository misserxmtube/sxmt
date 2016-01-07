window.SXMT=(function() {
    var SXMT = {}, localHistory = [];

    var sxmtStations = $("#sxmt-stations"), videoHistory = $("#videoHistory");
    var jScrollPaneOptions = {
        stickToBottom: true,
        verticalGutter: 0,
        horizontalGutter: 0
    };

    var initJScrollPane = function($elem) {
        var jsp;
        if (jsp = $elem.data("jsp")) {
            jsp.reinitialise();
        } else {
            $elem.jScrollPane(jScrollPaneOptions);
        }
    };

    /** Foundation-ize the document **/
    $(document).foundation({
        tab: {
            callback : function (tab) {
                if(sxmtStations.is(":visible")) initJScrollPane(sxmtStations);
                else if(videoHistory.is(":visible")) initJScrollPane(videoHistory);
            }
        }
    });

    /** Attach FastClick to the document body **/
    $(function() {FastClick.attach(document.body);});

    /** Resize Handler to Manage History Max-Height **/
    var resizeTimer;
    $(window).on("resize", function() {
        clearTimeout(resizeTimer);
        setTimeout(function() {
            //setHistoryMaxHeight(window.matchMedia("only screen and (min-width: 64.063em)"));
            if(sxmtStations.is(":visible")) initJScrollPane(sxmtStations);
            else if(videoHistory.is(":visible")) initJScrollPane(videoHistory);
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
        compileTemplate("nowPlayingText");
    })();

    SXMT.info = {};

    /** Initialize Video Player **/
    SXMT.playerReady = false;
    var onPlayerReady = function(event) {
        //$("#videoWrapper").addClass("noShow").removeClass("hideOffscreen");
//        event.target.playVideo();
        SXMT.playerReady = true;
        $("#sxmt-stations-overlay").remove();
    };
    var onPlayerStateChange = function(event) {
        if (SXMT.playerReady) {
            if (event.data == window.YT.PlayerState.PLAYING) {
                ctrlPlay.removeClass("fa-play").addClass("fa-pause");
                // TODO update status indicator
            } else if (event.data == window.YT.PlayerState.PAUSED) {
                // TODO update status indicator
                ctrlPlay.removeClass("fa-pause").addClass("fa-play");
            } else if (event.data == window.YT.PlayerState.ENDED) {
                //            SXMT.player.stopVideo();
                if (SXMT.repeat) {
                    SXMT.player.seekTo(0);
                    SXMT.player.playVideo();
                } else {
                    SXMT.loadSong(SXMT.info.currentStation, SXMT.info.currentSong.id, SXMT.info.currentSong.tweet);
                }
            }
        }
    };
    var onPlayerError = function(event) {
        console.error("Error playing video.", event);
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
            localHistory.shift();
        }
        var historyEntries = videoHistory.find(">.historyEntry");
        if (historyEntries.length > 0) historyEntries.first().remove();
    };
    window.onYouTubeIframeAPIReady = function() {
        SXMT.player = new window.YT.Player("videoPlayer", {
            //height: "600",
            height: "100%",
            width: "100%",
            playerVars: {
                autohide: 1,
                controls: 2,
                iv_load_policy: 3,
                modestbranding: 1,
                color: "white",
                theme: "dark",
                playsinline: 1,
                rel: 0,
                fs: 0,
                showinfo: 0
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
                SXMT.info.stations = data;
//                if (data.length < 1) {alert("No stations loaded!");return;}
                sxmtStations.html(templates.stations(data));
                if (sxmtStations.is(":visible")) initJScrollPane(sxmtStations);
            })
            .fail(function(data) {
                //alert("Could not load station list!");
                sxmtStations.empty();
                if (sxmtStations.is(":visible")) initJScrollPane(sxmtStations);
            });
    };

    /** Load Previous Song **/
    SXMT.loadPrevSong = function(station, song, tweet) {
        var history, matchIndex;
        if(sessionStorage) {
            history = sessionStorage.getItem("sxmt_history");
            if (history) history = JSON.parse(history);
        } else {
            history = localHistory;
        }
        if (history.length && (matchIndex = historyContainsEntry(song.tweet, song.id, station, history)) > 0) {
            SXMT.loadSongFromHistory(history[matchIndex - 1]);
        } else {
            var data = JSON.stringify({
                station: station,
                song: song,
                tweet: tweet
            });
            $.ajax({
                url: "./rest/prevsong",
                method: "POST",
                contentType: "application/json",
                data: data
            })
                .done(function (data) {
                    if (data.referenceTweet && !SXMT.info.referenceTweet) SXMT.info.referenceTweet = data.referenceTweet;
                    SXMT.info.currentSong = data;
                    $(document).trigger("loadVideo.sxmt");
                })
                .fail(function (data) {
                    alert("Could not load previous song!");
                });
        }
    };

    /** Show Current Song Info **/
    var nowPlayingImg = $("#sxmt-now-playing-img"), nowPlayingText = $("#sxmt-now-playing-text");
    var updateNowPlaying = function() {
        var data = SXMT.info.currentSong;
        nowPlayingImg.attr("src", data.thumbnail);
        nowPlayingText.html(templates.nowPlayingText(data));
    };

    /** Load Song **/
    SXMT.loadSong = function(station, song, tweet, ignoreReferenceTweet) {
        //if (SXMT.loadedFirst) {
        //    SXMT.info.previous = {station}
        //}
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
            .done(function (data) {
                if (data.referenceTweet && !SXMT.info.referenceTweet) SXMT.info.referenceTweet = data.referenceTweet;
                SXMT.info.currentSong = data;
                if (data.tweet) SXMT.addSongToHistory(SXMT.info.currentStation, SXMT.info.currentSong);
                $(document).trigger("loadVideo.sxmt");
            })
            .fail(function (data) {
                alert("Could not load next song!");
            });
    };

    /** Load Song from History **/
    SXMT.loadSongFromHistory = function(data) {
        var song = data.song;
        if (song.referenceTweet && !SXMT.info.referenceTweet) SXMT.info.referenceTweet = song.referenceTweet; //debating this. allows playing through history
        SXMT.info.currentSong = song;
        if (song.tweet) SXMT.addSongToHistory(SXMT.info.currentStation, SXMT.info.currentSong);
        $(document).trigger("loadVideo.sxmt");
        updateStationBackdrop(SXMT.info.currentStation.backdrop);
    };

    /** Load History **/
    SXMT.refreshHistory = function() {
        var history;
        if(sessionStorage) {
            history = sessionStorage.getItem("sxmt_history");
            if (history) history = JSON.parse(history);
        } else {
            history = localHistory;
        }
        videoHistory.html(templates.history(history));
        initJScrollPane(videoHistory);
    };

    var historyContainsEntry = function(tweet, video, station, history) {
        var index = -1;//, entryTweet = entry.song.tweet;//entryJson = JSON.stringify(entry);
        for (var i = 0, l = history.length; i < l; i ++) {
            //if (entryJson === JSON.stringify(history[i])) {
            //if (entryTweet === history[i].song.tweet) {
            var entry = history[i];
            if (station === entry.station && tweet === entry.song.tweet && video === entry.song.id) {
                index = i;
                break;
            }
        }
        return index;
    };

    /** Add Song to History **/
    SXMT.addSongToHistory = function(station, song) {
        if (!(station && song && song.tweet && song.id && song.song)) return;
        var MAX_HISTORY_ENTRIES = 100, existsAtIndex = -1,
            entry = {
                station: station,
                song: song
            };
        // Update session history
        if(sessionStorage) {
            var history = sessionStorage.getItem("sxmt_history");
            if (history) {
                history = JSON.parse(history);
                /*if ((existsAtIndex = historyContainsEntry(entry, history)) > -1) history.splice(existsAtIndex, 1);
                 else */if (history.length === MAX_HISTORY_ENTRIES) history.pop();
            } else {
                history = [];
            }
            history.unshift(entry);
            sessionStorage.setItem("sxmt_history", JSON.stringify(history));
        } else {
            if (localHistory.length === MAX_HISTORY_ENTRIES) localHistory.pop();
            localHistory.unshift(entry);
        }
        // Update UI history
        var historyEntries = videoHistory.find(">.historyEntry");
        /*if (existsAtIndex > -1) {
         var duplicate = historyEntries.get(existsAtIndex);
         if (duplicate) duplicate.remove();
         } else */if (historyEntries.length === MAX_HISTORY_ENTRIES) {
            historyEntries.last().remove();
        }
        var videoHistoryApi = videoHistory.data("jsp");
        videoHistoryApi.getContentPane().prepend(templates.historyEntry(entry));
        videoHistoryApi.reinitialise();
    };

    $(document).one("loadVideo.sxmt", function() {
        $("#videoHeader").removeClass("noMargin");
        $("#videoWrapper").removeClass("noShow");
        $("#sxmt-video-overlay").hide();
        $("#sxmt-now-playing").show();
        $("#sxmt-control-bar").show();
        $("#sxmt-content-pane").attr("station-selected", true);
        $("#sxmt-stations-panel").find("> div.tabs-content").attr("station-selected", true);
        //$("#videoSkip").show();
        //setHistoryMaxHeight(window.matchMedia("only screen and (min-width: 64.063em)"));
        //SXMT.refreshHistory();
    });
    $(document).on("loadVideo.sxmt", function() {
        document.getElementById("videoHeader").innerHTML = templates.videoHeader(SXMT.info.currentSong);
        SXMT.player.loadVideoById(SXMT.info.currentSong.id);
        updateNowPlaying();
    });

    /** Set Station Backdrop **/
    var updateStationBackdrop = function(imgUrl) {
        $("sxmtBody").css("background-image", "url('" + imgUrl + "'),url('images/noImg.png')");
    };

    /** Initialize Station List **/
    var stationSelected = function() {
        var $this = $(this);
        var tmp = $this.attr("data-station");
        if (SXMT.info.currentStation !== tmp) {
            SXMT.info.lastStation = SXMT.info.currentStation;
            SXMT.info.currentStation = tmp;
            SXMT.loadSong(SXMT.info.currentStation);
            updateStationBackdrop(SXMT.info.currentStation.backdrop);
            //$("html, body").animate({scrollTop: 0}, "slow");
        }
    };
    sxmtStations.on("click", ".station", stationSelected);
    SXMT.refreshStations();

    /** Initialize History **/
    videoHistory.on("click", ".historyEntry", function(event) {
        event.preventDefault();
        var $this = $(this), history;
        if(sessionStorage) {
            history = sessionStorage.getItem("sxmt_history");
            if (history) history = JSON.parse(history);
        } else {
            history = localHistory;
        }
        // Added reference tweet id so we don't end up playing through the history since the video selected
        if (SXMT.info.currentSong && !SXMT.info.referenceTweet) SXMT.info.referenceTweet = SXMT.info.currentSong.tweet;
        var station = $this.attr("data-station"), song = $this.attr("data-song"), tweet = $this.attr("data-tweet");
        if (SXMT.info.currentStation !== station) {
            SXMT.info.lastStation = SXMT.info.currentStation;
            SXMT.info.currentStation = station;
        }
        var matchIndex;
        //$this.remove();
        if ((matchIndex = historyContainsEntry(tweet, song, station, history)) > -1) {
            SXMT.loadSongFromHistory(history[matchIndex]);
        } else {
            SXMT.loadSong(station, song, tweet, true);
        }
        //$("html, body").animate({scrollTop: 0}, "slow");
    });
    SXMT.refreshHistory();

    /** Setup Video Controls **/
    var ctrlPrev = $("#sxmt-prev"),
        ctrlPlay = $("#sxmt-play"),
        ctrlNext = $("#sxmt-next"),
        ctrlRepeat = $("#sxmt-repeat"),
        ctrlLatest = $("#sxmt-latest"),
        ctrlMenu = $("#sxmt-menu"),
        ctrlGroup = $(ctrlPrev).add(ctrlPlay).add(ctrlNext).add(ctrlRepeat).add(ctrlLatest);

    $(ctrlGroup).add(ctrlMenu).on("click.sxmt-station-selected", function(event) {
        if (SXMT.info.currentStation) {
            $(ctrlGroup).add(ctrlMenu).off("click.sxmt-station-selected");
        } else {
            event.preventDefault();
            event.stopImmediatePropagation();
        }
    });

    ctrlPrev.on("click", function() {SXMT.loadPrevSong(SXMT.info.currentStation, SXMT.info.currentSong.id, SXMT.info.currentSong.tweet);});
    ctrlPlay.on("click", function() {
        var $this = $(this);
        if ($this.attr("paused")) {
            $this.removeAttr("paused").attr("title", "Pause");
            SXMT.player.playVideo();
            $this.removeClass("fa-play").addClass("fa-pause");
        } else {
            $this.attr("paused", "paused").attr("title", "Play");
            SXMT.player.pauseVideo();
            $this.removeClass("fa-pause").addClass("fa-play");
        }
    });
    ctrlNext.on("click", function() {SXMT.loadSong(SXMT.info.currentStation, SXMT.info.currentSong.id, SXMT.info.currentSong.tweet);});
    ctrlRepeat.on("click", function() {
        SXMT.repeat = !SXMT.repeat;
        $(this).toggleClass("sxmt-repeat");
    });
    ctrlLatest.on("click", function() {SXMT.loadSong(SXMT.info.currentStation);});

    ctrlMenu.on("click", function() {
        $(ctrlGroup).toggleClass("hideOnMobile");
        ctrlMenu.toggleClass("fa-caret-square-o-up fa-caret-square-o-down")
    });

    return SXMT;
})();