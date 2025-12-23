package com.example.sawitku.ui.komunitas

data class Post(
    var key: String = "",
    var authorName: String = "",
    var authorRole: String = "",
    var time: String = "",
    var tag: String = "",
    var title: String = "",
    var content: String = "",
    var imageUri: String = "",
    var likes: Int = 0,
    var comments: Int = 0
) {
    constructor() : this("", "", "", "", "", "", "", "", 0, 0)
}