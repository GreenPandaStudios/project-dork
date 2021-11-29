import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.javacord.api.DiscordApi;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;

enum AudioSourceType {
    LOCAL_SOUND_EFFECT, YOUTUBE_MUSIC
}

public class AudioManager {
    static YoutubeAudioSourceManager youtubeManager;
    static LocalAudioSourceManager localManager;
    static AudioPlayerManager playerManager;
    static AudioPlayer player;
    static TrackScheduler trackScheduler;
    static long currentMusicPos;

    static void startAudio(DiscordApi api, ServerVoiceChannel validVoiceChannel) {
        validVoiceChannel.connect().thenAccept(audioConnection -> {
            // Create a player manager
            playerManager = new DefaultAudioPlayerManager();

            // Create YouTube and local sources
            youtubeManager = new YoutubeAudioSourceManager();
            localManager = new LocalAudioSourceManager();

            // Create player
            player = playerManager.createPlayer();

            // Add listener for player events
            trackScheduler = new TrackScheduler();
            currentMusicPos = 0;
            player.addListener(trackScheduler);

            // Create an audio source and add it to the audio connection's queue
            AudioSource source = new LavaplayerAudioSource(api, player);
            audioConnection.setAudioSource(source);

            // Start playing fantasy music
            // IF of 'PNaTS4LkbG0'
            playNewSound(AudioSourceType.YOUTUBE_MUSIC, "https://www.youtube.com/watch?v=PNaTS4LkbG0&ab_channel=Elysio");

        }).exceptionally(e -> {
            // Failed to connect to voice channel (no permissions?)
            e.printStackTrace();
            return null;
        });
    }

    static void playNewSound(AudioSourceType sourceType, String audioName) {

        if (sourceType == AudioSourceType.LOCAL_SOUND_EFFECT) {
            currentMusicPos = player.getPlayingTrack().getPosition();
            playerManager.registerSourceManager(localManager);
        } else if (sourceType == AudioSourceType.YOUTUBE_MUSIC) {
            playerManager.registerSourceManager(youtubeManager);
        } else {
            return;
        }

        playerManager.loadItem(audioName, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                player.playTrack(track);
                if (sourceType == AudioSourceType.YOUTUBE_MUSIC) {
                    track.setPosition(AudioManager.currentMusicPos);
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    player.playTrack(track);
                }
            }

            @Override
            public void noMatches() {
                System.out.println("No matches to audio " + audioName);
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                System.out.println("Everything failed to load for audio " + audioName);
            }
        });

    }

    static String getLocalPath(String toLocalFile) {
        return String.format("src/main/resources/audio/%s", toLocalFile);
    }
}

// Listener for player events
class TrackScheduler extends AudioEventAdapter {
    @Override
    // Sound effect ended, play music again, where it left off
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // Start the music again if the music wasn't what just ended
        if (endReason.mayStartNext ) {
            AudioManager.playNewSound(AudioSourceType.YOUTUBE_MUSIC, "https://www.youtube.com/watch?v=PNaTS4LkbG0&ab_channel=Elysio");
        }
    }
}