package com.example.server.game;

import java.util.List;

public class WinningPattern {

    public final static List<List<Integer>> patterns = List.of(
            List.of(0,1,2),
            List.of(3,4,5),
            List.of(6,7,8),
            List.of(0,3,6),
            List.of(1,4,7),
            List.of(2,5,8),
            List.of(0,4,8),
            List.of(2,4,6)
    );
}
