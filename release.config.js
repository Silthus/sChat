const ref = process.env.GITHUB_REF;
const branch = ref.split('/').pop();

const config = {
    branches: [
        'main',
        { name: "release", prerelease: "rc", channel: "rc" },
        { name: "next", prerelease: "SNAPSHOT", channel: "next" }
    ],
    plugins: [
        '@semantic-release/commit-analyzer',
        '@semantic-release/release-notes-generator'
    ],
}

if (config.branches.some(it => it === branch || (it.name === branch && it.prerelease))) {
    config.plugins.push(
        ["@google/semantic-release-replace-plugin", {
            "replacements": [
                {
                    "files": ["gradle.properties"],
                    "from": "version = .*",
                    "to": "version = ${nextRelease.version.replace(/-SNAPSHOT.*/,'-SNAPSHOT')}",
                    "countMatches": true
                }
            ]
        }],
        ['@semantic-release/git', {
            assets: [
                "gradle.properties"
            ]
        }],
        ["@semantic-release/exec", {
            "publishCmd": "./gradlew -PforceSign=true publish --no-daemon"
        }],
        ["@semantic-release/github", {
            "assets": [
                {
                    "path": "{bukkit,velocity,bungeecord}/build/libs/*+([0-9]|SNAPSHOT).jar"
                }
            ]
        }]
    )
} else if (config.branches.some(it => it === branch || (it.name === branch && !it.prerelease))) {
    config.plugins.push('@semantic-release/changelog',
        ["@google/semantic-release-replace-plugin", {
            "replacements": [
                {
                    "files": ["gradle.properties"],
                    "from": "version = .*",
                    "to": "version = ${nextRelease.version}",
                    "countMatches": true
                },
                {
                    "files": [
                        "**/src/main/java/**/*.java"
                    ],
                    "from": "@since next",
                    "to": "@since ${nextRelease.version}",
                    "countMatches": true
                },
                {
                    "files": [
                        "**/src/main/java/**/*.java"
                    ],
                    "from": "since = \"next\"",
                    "to": "since = \"${nextRelease.version}\"",
                    "countMatches": true
                },
                {
                    "files": [
                        "docs/**/*.md"
                    ],
                    "from": "[next]: https://github.com/sVoxelDev/sChat/releases/latest",
                    "to": "[next]: https://github.com/sVoxelDev/sChat/releases/latest\n[${nextRelease.version}]: https://github.com/sVoxelDev/sChat/releases/tag/${nextRelease.version}",
                    "countMatches": true
                },
                {
                    "files": [
                        "docs/**/*.md"
                    ],
                    "from": "[:octicons-milestone-24: next][next]",
                    "to": "[:octicons-milestone-24: ${nextRelease.version}][${nextRelease.version}]",
                    "countMatches": true
                }
            ]
        }],
        ['@semantic-release/git', {
            assets: [
                "**/src/main/java/**/*.java",
                "docs/**/*.md",
                "gradle.properties",
                "CHANGELOG.md"
            ]
        }],
        ["@semantic-release/exec", {
            "publishCmd": "./gradlew -PforceSign=true publish closeAndReleaseSonatypeStagingRepository --no-daemon",
            "successCmd": "mike deploy --prefix docs --push --update-aliases ${nextRelease.version.replace(/\.\d+$/, '')} latest"
        }],
        ["@semantic-release/github", {
            "assets": [
                {
                    "path": "{bukkit,velocity,bungeecord}/build/libs/*+([0-9]|SNAPSHOT).jar"
                }
            ]
        }]
    )
}

module.exports = config