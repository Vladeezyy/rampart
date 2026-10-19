# Rampart

[![License LGPL-3.0](https://img.shields.io/badge/license-LGPL--3.0-blue)](LICENSE)

Entity model and animation library for our ports of Alexthe666's mods (Rats, later Ice and Fire), for **NeoForge and
Fabric**, Minecraft 1.21.1 (26.x on branches `mc/<version>`).

It carries the parts of [Citadel](https://github.com/AlexModGuy/Citadel) (by Alexthe666, gegy1000 and iLexiconn,
LGPL-3.0) that those mods use: `AdvancedEntityModel`, `AdvancedModelBox`, `BasicModelPart`, `ModelAnimator`,
`Animation`, `AnimationHandler`, `IAnimatedEntity` (package `dev.vladeezyy.rampart`). Citadel itself has no Fabric and
no 26.x build. Rampart has its own mod id and package, so it never clashes with Citadel in the same game.

Differences from Citadel:
- the animation packet goes only to the players that track the entity (Citadel sends it to every player on the server,
  in every dimension), as a pair of VarInts;
- model boxes render without allocating two vectors per vertex (the client check compares every vertex with Citadel's
  math);
- no animation start/tick events.

## Use

```groovy
repositories { mavenLocal() } // ./gradlew publishToMavenLocal in this repo
dependencies {
    implementation "dev.vladeezyy.rampart:rampart-neoforge:1.0.0+1.21.1"      // NeoForge
    modImplementation "dev.vladeezyy.rampart:rampart-fabric:1.0.0+1.21.1"     // Fabric
    compileOnly "dev.vladeezyy.rampart:rampart-common:1.0.0+1.21.1"           // common code
}
```

## Tests

`tools/selftest.sh` (server: animation lifecycle, packet codec, `LOADER=fabric` for Fabric) and `tools/scenetest.sh`
(client: renders a model into a recording buffer and compares with Citadel's math).

## License

LGPL-3.0-only. Copyright (c) 2026 Vladeezyy; the model and animation code comes from Citadel (LGPL-3.0). Forks are
welcome under the same license with credit; please download Rampart only from its official pages.

<details>
<summary>По-русски</summary>

Библиотека моделей и анимаций для наших портов модов Alexthe666 (Rats, позже Ice and Fire) на NeoForge и Fabric.
Внутри — нужная этим модам часть Citadel (LGPL-3.0): у самой Citadel нет версий для Fabric и 26.x. Свой mod id и
пакет, поэтому с Citadel в одной сборке не конфликтует. Отличия: пакет анимации уходит только игрокам рядом с
существом, коробки моделей рисуются без выделения памяти на каждую вершину.

</details>
