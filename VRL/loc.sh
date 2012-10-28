find src -type f -not -regex '\./\.git.*' | xargs cat | wc -l
