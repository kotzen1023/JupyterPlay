package com.seventhmoon.jupyterplay.media.library

import android.content.Context

import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import com.seventhmoon.jupyterplay.R
import com.seventhmoon.jupyterplay.media.extensions.*

class BrowseTree(context: Context, musicSource: MusicSource) {
    private val mTAG = BrowseTree::class.java.name
    private val mediaIdToChildren = mutableMapOf<String, MutableList<MediaMetadataCompat>>()

    /**
     * Whether to allow clients which are unknown (non-whitelisted) to use search on this
     * [BrowseTree].
     */
    val searchableByUnknownCaller = true

    /**
     * In this example, there's a single root node (identified by the constant
     * [UAMP_BROWSABLE_ROOT]). The root's children are each album included in the
     * [MusicSource], and the children of each album are the songs on that album.
     * (See [BrowseTree.buildAlbumRoot] for more details.)
     *
     * TODO: Expand to allow more browsing types.
     */
    init {
        Log.d(mTAG, "->init")
        val rootList = mediaIdToChildren[UAMP_BROWSABLE_ROOT] ?: mutableListOf()

        val recommendedMetadata = MediaMetadataCompat.Builder().apply {
            id = UAMP_RECOMMENDED_ROOT
            title = context.getString(R.string.recommended_title)
            albumArtUri = imageUriRoot +
                    context.resources.getResourceEntryName(R.drawable.ic_recommended)
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        val albumsMetadata = MediaMetadataCompat.Builder().apply {
            id = UAMP_ALBUMS_ROOT
            title = context.getString(R.string.albums_title)
            albumArtUri = imageUriRoot + context.resources.getResourceEntryName(R.drawable.ic_album)
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        rootList += recommendedMetadata
        rootList += albumsMetadata
        mediaIdToChildren[UAMP_BROWSABLE_ROOT] = rootList

        musicSource.forEach { mediaItem ->
            val albumMediaId = mediaItem.album.urlEncoded
            val albumChildren = mediaIdToChildren[albumMediaId] ?: buildAlbumRoot(mediaItem)
            albumChildren += mediaItem

            // Add the first track of each album to the 'Recommended' category
            if (mediaItem.trackNumber == 1L){
                val recommendedChildren = mediaIdToChildren[UAMP_RECOMMENDED_ROOT]
                    ?: mutableListOf()
                recommendedChildren += mediaItem
                mediaIdToChildren[UAMP_RECOMMENDED_ROOT] = recommendedChildren
            }
        }
    }

    /**
     * Provide access to the list of children with the `get` operator.
     * i.e.: `browseTree\[UAMP_BROWSABLE_ROOT\]`
     */
    operator fun get(mediaId: String) = mediaIdToChildren[mediaId]

    /**
     * Builds a node, under the root, that represents an album, given
     * a [MediaMetadataCompat] object that's one of the songs on that album,
     * marking the item as [MediaItem.FLAG_BROWSABLE], since it will have child
     * node(s) AKA at least 1 song.
     */
    private fun buildAlbumRoot(mediaItem: MediaMetadataCompat) : MutableList<MediaMetadataCompat> {
        Log.d(mTAG, "->buildAlbumRoot")
        val albumMetadata = MediaMetadataCompat.Builder().apply {
            id = mediaItem.album.urlEncoded
            title = mediaItem.album
            artist = mediaItem.artist
            albumArt = mediaItem.albumArt
            albumArtUri = mediaItem.albumArtUri.toString()
            flag = MediaItem.FLAG_BROWSABLE
        }.build()

        // Adds this album to the 'Albums' category.
        val rootList = mediaIdToChildren[UAMP_ALBUMS_ROOT] ?: mutableListOf()
        rootList += albumMetadata
        mediaIdToChildren[UAMP_ALBUMS_ROOT] = rootList

        // Insert the album's root with an empty list for its children, and return the list.
        return mutableListOf<MediaMetadataCompat>().also {
            mediaIdToChildren[albumMetadata.id] = it
        }
    }
}

const val UAMP_BROWSABLE_ROOT = "/"
const val UAMP_EMPTY_ROOT = "@empty@"
const val UAMP_RECOMMENDED_ROOT = "__RECOMMENDED__"
const val UAMP_ALBUMS_ROOT = "__ALBUMS__"

const val MEDIA_SEARCH_SUPPORTED = "android.media.browse.SEARCH_SUPPORTED"

const val imageUriRoot = "android.resource://com.example.android.uamp.next/drawable/"