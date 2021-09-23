package jp.techacademy.yoshikazu.takahashi.apiapp

interface FragmentCallback {
    fun onAddFavorite(shop: Shop)
    fun onDeleteFavorite(id: String)
}