import Cell from "./Cell.js";

export default function Minesweeper(rows, cols, bombs) {
    var rows = rows;
    var cols = cols;
    var bombs = bombs;
    var matrix = createMatrix();
    function getRows() {
        return rows;
    }
    function getCols() {
        return cols;
    }
    function getBombs() {
        return bombs;
    }
    function getCell(row, col) {
        return matrix[row][col];
    }
    function createMatrix() {
        let arr = [];
        for (let i = 0; i < rows; ++i) {
            arr.push(new Array(cols).fill(Cell.EMPTY));
        }
        return arr;
    }
    function fillBombs() {
        for (let i = 0; i < bombs; ) {
            let row = Math.floor((Math.random() * rows));
            let col = Math.floor((Math.random() * cols));
            if (matrix[row][col] === Cell.EMPTY) {
                matrix[row][col] = Cell.BOMB;
                i++;
            }
        }
        setNumbers();
    }
    function setNumbers() {
        for (let i = 0; i < rows; i++) {
            for (let j = 0; j < cols; j++) {
                if (matrix[i][j] === Cell.EMPTY) {
                    matrix[i][j] = countPosition(i, j);
                }
            }
        }
    }
    function countPosition(row, col) {
        let count = 0;
        for (let i = row - 1; i <= row + 1; i++) {
            for (let j = col - 1; j <= col + 1; j++) {
                if (i >= 0 && i < rows && j >= 0 && j < cols && matrix[i][j] === Cell.BOMB) {
                    count++;
                }
            }
        }
        return count;
    }
    return {getRows, getCols, getBombs, getCell, fillBombs};
}
