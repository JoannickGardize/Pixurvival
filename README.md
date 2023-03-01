# Pixurvival

Pixurvival is a pixelated top-down mega sandbox written in Java. Play the vanilla content or create your own content
pack with the editor.

*[Itch.io presentation & download page](https://sharkhendrix.itch.io/pixurvival)*

*[Trello board](https://trello.com/b/84pvuPq8/pixurvival-dev)*

## Build and run

You must use a JDK 8 to build the project.

Run configurations for IDEs:
|Name|Module|Main class|Mandatory arguments|Recommended arguments|Working directory|
|----|------|----------|-------------------|---------------------|-----------------|
|Game|gdx-desktop|com.pixurvival.desktop.DesktopLauncher||redirectErrorToFile=false zoomEnabled=true|gdx-core\assets|
|Editor|contentpack-editor|com.pixurvival.contentPackEditor.ContentPackEditor|contentPackDirectory="gdx-core/assets/contentPacks"|||
|Server|server|com.pixurvival.server.console.ServerConsole|contentPackDirectory="gdx-core/assets/contentPacks"|||

build the distrubtion package : `./gradlew packageDistribution`

## Contributing

Contribution guide TODO. For now, join the [Discord](https://discord.gg/VXEpJhJ) to discuss if you want to contribute.
