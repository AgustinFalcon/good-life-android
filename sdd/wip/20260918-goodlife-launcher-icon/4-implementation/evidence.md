# Evidence — build and clean-install validation

- `:app:assembleDebug` completed successfully.
- Packaged manifest inspection confirmed `icon` resolves to `ic_logo_install` and `roundIcon` to `ic_logo_install_round`.
- The previous GoodLife package was removed and this debug APK was cleanly installed, invalidating launcher cache. No account was signed in and no app data was preserved.
- Initial CI correctly rejected a baseline mismatch after the manifest swap; this was not waived. The correction removes the orphaned generic launcher family, adds the round GoodLife monochrome layer, and uses an immutable #60 manifest to remove exactly two stale baseline symbols per variant.
- Local Python guardrails passed: resource inventory (4 symbols) plus 16 inventory tests and 16 fail-closed reducer tests. The updated PR CI is the authority for lint debug/release and unsigned release assembly.
- CI updated: workflow [#35370856667](https://github.com/AgustinFalcon/good-life-android/actions/runs/35370856667) passed both the logic/coverage and lint/unsigned-release jobs for `dc11c02`.
- Independent code review approved the final resource, baseline and workflow changes.
- Visual acceptance: the user confirmed that Pixel Launcher shows the installed GoodLife entry with the GoodLife G. No screenshot, device identifier, account data or credentials are committed.
