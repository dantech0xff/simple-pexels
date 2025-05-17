# Simple-Pexels Photo Search App
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/dantech0xff/simple-pexels)

A simple Android application that allows users to search for and browse images using the Pexels API. This project is developed focusing on modern Android development practices, clean architecture, and a user-friendly interface.

## Architecture and Technical Choices

The application follows the **MVVM (Model-View-ViewModel)** architecture pattern, incorporating principles from **Clean Architecture** to ensure a clear separation of concerns, improve testability, and facilitate scalability.

- **Overall Structure:**

  - **Presentation Layer (UI):** Built with Jetpack Compose for a declarative and modern UI. ViewModels are used to manage UI state and handle UI-related business logic.
  - **Domain Layer:** This layer contains Use Cases (e.g., `QueryPhotosByKeywordUseCase`) that encapsulate specific business logic, acting as an intermediary between the Presentation and Data layers.
  - **Data Layer:** Repositories serve as the single source of truth for data, abstracting data sources, primarily the remote Pexels API. It also includes a simple local data store (`DataStore Preferences`) for managing favorite photos.

- **Key Technical Choices & Libraries:**
  - **Language:** [Kotlin](https://kotlinlang.org/) - Chosen for its conciseness, null safety, and excellent integration with modern Android development.
  - **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) - For building a reactive and declarative UI with less boilerplate code.
  - **Asynchronous Programming:** [Kotlin Coroutines & Flow](https://kotlinlang.org/docs/coroutines-guide.html) - Used extensively for managing background tasks, API calls, and handling data streams efficiently and reactively.
  - **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - For managing dependencies throughout the application, improving code organization, and simplifying testing.
  - **Networking:** [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for making API calls to the Pexels API.
    - **JSON Parsing:** [Moshi](https://github.com/square/moshi) - For efficient serialization and deserialization of JSON data from the Pexels API.
  - **Image Loading:** [Coil 3](https://coil-kt.github.io/coil/) - An image loading library for Kotlin Coroutines, chosen for its performance, modern API, and ease of use with Jetpack Compose. It's configured with memory and disk caching.
  - **Navigation:** [Jetpack Compose Navigation](https://developer.android.com/jetpack/compose/navigation) - For handling navigation between different screens (Search and Photo Detail) in a Compose-idiomatic way. Type-safe navigation is used by passing `Photo` objects as routes.
  - **Local Data Storage:** [Jetpack DataStore (Preferences)](https://developer.android.com/topic/libraries/architecture/datastore) - Used for persisting simple key-value data, specifically for storing the IDs of favorite photos.
  - **API:** [Pexels API](https://www.pexels.com/api/documentation/) - Used for fetching photos based on search queries.
  - **UI Enhancements:**
    - [Lottie for Android](https://airbnb.io/lottie/#/android) - Used for displaying animations (e.g., for empty or loading states).
    - **Edge-to-edge display** is enabled for a more immersive UI.

## Trade-offs Made

Given the limited development timeframe, certain trade-offs were made:

- **Error Handling:** Basic error handling is in place for API failures (e.g., displaying messages via Toast). However, more granular error messages or dedicated error screens for various scenarios (e.g., no network, specific API error codes) were not extensively implemented.
- **UI Polish & Animations:** The UI is functional and clean. Advanced animations, complex screen transitions beyond default Compose animations, and extensive theming (e.g., dynamic dark/light mode based on system settings is present, but further customization is limited) were kept minimal.
- **Testing Coverage:**
  - **Unit Tests:** Unit tests are present for UseCases (`QueryPhotosByKeywordUseCaseTest`), ViewModels (`SearchViewModelTest`), and data source/adapter logic (`PhotoDataSourceImplTest`, `PhotoAdapterTest`). Coverage focuses on core logic.
  - **UI Tests:** No extensive UI tests (e.g., using Espresso or Compose testing APIs for UI interactions) were developed due to time constraints. Only a basic instrumented test (`ExampleInstrumentedTest`) exists.
- **No Local Caching for API Responses:** API responses for photos are not cached locally (e.g., using Room). All data is fetched live from the Pexels API upon each request or when loading more results. Image caching is handled by Coil.
- **Limited Feature Set:**
  - The "Download" functionality in the photo detail screen is a placeholder and shows a Toast message indicating it's under development.
  - Search history is not implemented.
  - Advanced filtering or sorting options for search results are not available.
- **Code Comments and Documentation:** While the code has some comments, comprehensive KDoc documentation for all public classes and methods might be inconsistent.
- **Responsive Layouts:** The app primarily targets portrait phone layouts. While Compose offers some inherent responsiveness, specific optimizations for tablets or landscape orientations were not a primary focus.

## Instruction to Run the App

1.  **Prerequisites:**

    - Android Studio (latest stable version recommended, e.g., Android Studio Iguana | 2023.2.1 Patch 1 or newer).
    - Android SDK (compileSdk is set to 35, minSdk to 26).
    - A Pexels API Key.

2.  **Clone the Repository:**

    ```bash
    git clone https://github.com/dantech0xff/simple-pexels.git
    ```

3.  **API Key Setup:**

    - Obtain a free API key from the [Pexels API Documentation](https://www.pexels.com/api/documentation/#introduction).
    - Open the project in Android Studio.
    - Locate the `local.properties` file in the root of your project. If it doesn't exist, create it.
    - Add your Pexels API key and base URL to the `local.properties` file like this:
      ```properties
      pexels.key="YOUR_ACTUAL_PEXELS_API_KEY"
      pexels.base.url="[https://api.pexels.com/](https://api.pexels.com/)"
      ```
      Replace `"YOUR_ACTUAL_PEXELS_API_KEY"` with your real key. The base URL is typically `https://api.pexels.com/`.
    - The project is already configured in `app/build.gradle.kts` to read these values and make them available via `BuildConfig`. The `PexelsAuthInterceptor.kt` uses `BuildConfig.PEXELS_API_KEY`.

4.  **Open in Android Studio:**

    - Launch Android Studio.
    - Select "Open" and navigate to the cloned `Simple-Pexels` project directory.

5.  **Build and Run:**
    - Wait for Android Studio to sync and build the project (this may take a few moments as it downloads dependencies).
    - Select an Android device (physical or emulator).
    - Click the "Run" button (green play icon) in Android Studio or use the `Shift + F10` shortcut.

## Things to Add or Improve with More Time

- **PlantUML Diagrams:**
  - Add PlantUML diagrams to the README to visualize the architecture and data flow.
  - Include sequence diagrams for key interactions (e.g., API calls, data flow).
- **Add Images, GIFs, or Videos to the README:**
  - Include screenshots or GIFs of the app in action to provide a visual overview of its functionality and design.
- **CI/CD Pipeline:**
  - Set up a Continuous Integration/Continuous Deployment (CI/CD) pipeline (e.g., using GitHub Actions Jenkins).
  - Automate builds, running unit tests, and potentially UI tests on every push or pull request.
  - Automate deployment to testing tracks or app stores for release candidates or production builds.
- **Parameterize Build Variants:**
  - Configure build variants (e.g., debug, staging, release) with different parameters.
- **Comprehensive Testing:**
  - Increase unit test coverage for all ViewModels, UseCases, and Repositories, covering more edge cases.
  - Implement UI tests using Jetpack Compose testing APIs to verify UI interactions and screen states.
- **Advanced UI/UX:**
  - Implement more sophisticated UI animations and screen transitions.
  - Enhance theming, potentially with more customizable options or better support for different system themes.
  - Improve accessibility features (e.g., content descriptions, focus order).
- **Offline Support for API Data:**
  - Implement local caching of API responses (photo lists for searches) using a database like Room. This would allow users to view previously loaded photos even when offline and reduce API calls.
- **Search History:**
  - Implement a feature to save and display recent search queries, possibly using DataStore or Room.
- **Pull-to-Refresh:**
  - Add pull-to-refresh functionality on the search results screen to manually refresh the current search.
- **Responsive/Adaptive Layouts:**
  - Further enhance layouts to be fully adaptive for various screen sizes and orientations, including tablets and foldable devices.
- **Detailed Error States & Recovery:**
  - Provide more specific error messages and user-friendly recovery options for different types of network or API errors (e.g., a dedicated screen or a more informative inline message with a retry button).
- **Performance Optimization:**
  - Profile the app for performance bottlenecks (e.g., list scrolling, image loading, data processing) and apply optimizations.
- **Modularization:**
  - For a larger application, consider breaking down features (e.g., search, photo detail, favorites) into separate Gradle modules to improve build times and code organization.
- **Complete Download Functionality:**
  - Implement the actual photo download feature, allowing users to save original quality images to their device storage. This would involve handling permissions and file operations.
- **User Authentication & Cloud Sync for Favorites:**
  - If users could log in, favorites could be synced across devices.
- **Input Validation:**
  - More robust input validation for the search query (e.g., handling excessively long queries, disallowing certain characters if needed).
- **Localization:**
  - Add support for multiple languages.
- **Code Quality & Linting:**
    - Integrate a linter (e.g., Ktlint, Detekt) to enforce code style and quality checks.
    - Set up a code formatter (e.g., Spotless) to ensure consistent code formatting across the project.
