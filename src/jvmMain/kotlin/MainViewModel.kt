import data.repository.DeckRepository

class MainViewModel(private val deckRepository: DeckRepository) {
    fun testShuffle() {
        deckRepository.shuffleAndGetDeckState()
    }
}