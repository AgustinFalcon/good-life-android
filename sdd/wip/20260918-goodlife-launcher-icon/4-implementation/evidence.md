# Evidence — build and clean-install validation

- `:app:assembleDebug` completed successfully.
- Packaged manifest inspection confirmed `icon` resolves to `ic_logo_install` and `roundIcon` to `ic_logo_install_round`.
- The previous GoodLife package was removed and this debug APK was cleanly installed, invalidating launcher cache. No account was signed in and no app data was preserved.
- Initial CI correctly rejected a baseline mismatch after the manifest swap; this was not waived. The correction removes the orphaned generic launcher family, adds the round GoodLife monochrome layer, and uses an immutable #60 manifest to remove exactly two stale baseline symbols per variant.
- Local Python guardrails passed: resource inventory (4 symbols) plus 16 inventory tests and 16 fail-closed reducer tests. The updated PR CI is the authority for lint debug/release and unsigned release assembly.
- Remaining evidence: successful updated CI, independent review, and visual confirmation in Pixel Launcher that the installed entry shows the GoodLife G. No screenshot, device identifier or account data is committed.
