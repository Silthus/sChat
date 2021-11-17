# [1.0.0-beta.7](https://github.com/Silthus/sChat/compare/v1.0.0-beta.6...v1.0.0-beta.7) (2021-11-16)


### Bug Fixes

* **cmd:** add missing permission checks to channel click leave/join events ([151a687](https://github.com/Silthus/sChat/commit/151a68701a1a40a7d06d0d61a306f7cae642db39)), closes [#41](https://github.com/Silthus/sChat/issues/41)


### Features

* **chat:** add an option to disable the channel footer ([6837db2](https://github.com/Silthus/sChat/commit/6837db20cbbb5871c9a6f808bf75c408979ad8b3))
* **chat:** clicking player names starts private conversation ([7b0ff8e](https://github.com/Silthus/sChat/commit/7b0ff8eddd35fa959707b2415688d3a338015c15)), closes [#39](https://github.com/Silthus/sChat/issues/39)

# [1.0.0-beta.6](https://github.com/Silthus/sChat/compare/v1.0.0-beta.5...v1.0.0-beta.6) (2021-11-16)


### Features

* persist active channel across restarts ([c8b4279](https://github.com/Silthus/sChat/commit/c8b427987c87888b0a46578ba9b9f8acc168a8b9)), closes [#15](https://github.com/Silthus/sChat/issues/15)
* remember active channel across restarts ([0092d34](https://github.com/Silthus/sChat/commit/0092d3430db3b97ae427d65c8c2973ad72acf658))

# [1.0.0-beta.5](https://github.com/Silthus/sChat/compare/v1.0.0-beta.4...v1.0.0-beta.5) (2021-11-15)


### Bug Fixes

* **cmd:** reload not removing console target from channel if `console: false` ([f77794f](https://github.com/Silthus/sChat/commit/f77794f24f17480a50d8fb901196dab0124c5672))


### Features

* **config:** add name config to console ([c30afb1](https://github.com/Silthus/sChat/commit/c30afb1cf17436a0bd8233b96f1c72dfd64d38fb))

# [1.0.0-beta.4](https://github.com/Silthus/sChat/compare/v1.0.0-beta.3...v1.0.0-beta.4) (2021-11-15)


### Bug Fixes

* **platform:** system messages display under the channel tabs ([cac4ffb](https://github.com/Silthus/sChat/commit/cac4ffbc62b48b981be3b68b79d595d93ecbd87b)), closes [#36](https://github.com/Silthus/sChat/issues/36)

# [1.0.0-beta.3](https://github.com/Silthus/sChat/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2021-11-15)


### Bug Fixes

* **platform:** drop paper support and shade adventure-text directly ([76c25cc](https://github.com/Silthus/sChat/commit/76c25cc76d29790d4b27f9976caf6ba3393e7f03)), closes [#34](https://github.com/Silthus/sChat/issues/34)

# [1.0.0-beta.2](https://github.com/Silthus/sChat/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2021-11-14)


### Features

* **cmd:** add non-disruptive `/schat reload` command ([486eb48](https://github.com/Silthus/sChat/commit/486eb48a7365e45ce0588aee598f6d6756422c79)), closes [#10](https://github.com/Silthus/sChat/issues/10)
* **integrations:** add optional PlaceholderAPI support ([4c5ed02](https://github.com/Silthus/sChat/commit/4c5ed02dd21e38c99313f0a739f57ef34c2ff0ff)), closes [#11](https://github.com/Silthus/sChat/issues/11)

# 1.0.0-beta.1 (2021-11-14)


### Bug Fixes

* **release:** lowercase artifactid and group ([688f377](https://github.com/Silthus/sChat/commit/688f3777abdbc0f7efe797d87dac96143d40088a))
* unsubscribe all from channel when removed ([f6e3ed1](https://github.com/Silthus/sChat/commit/f6e3ed15a6fc95195da5b8fdae0e41e98400b300))


### Features

* add icon to leave a conversation ([31db6dc](https://github.com/Silthus/sChat/commit/31db6dc47300f6061b25f87aeffec192a6fc68c7))
* initial beta release ([8cac128](https://github.com/Silthus/sChat/commit/8cac1281e9530898bcef3c799455f61d6942a91a))
* **view:** add unread message indicator to channels ([bf720d4](https://github.com/Silthus/sChat/commit/bf720d450184a7c6e51731fe2fbb6e31fba2adb4))