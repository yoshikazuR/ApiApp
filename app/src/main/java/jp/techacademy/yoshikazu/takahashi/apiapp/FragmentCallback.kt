package jp.techacademy.yoshikazu.takahashi.apiapp

interface FragmentCallback {
    fun onClickItem(shop: Shop)
    fun onAddFavorite(shop: Shop)
    fun onDeleteFavorite(id: String)
}