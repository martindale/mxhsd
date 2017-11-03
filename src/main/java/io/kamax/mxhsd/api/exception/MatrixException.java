/*
 * mxhsd - Corporate Matrix Homeserver
 * Copyright (C) 2017 Maxime Dor
 *
 * https://www.kamax.io/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.kamax.mxhsd.api.exception;

public class MatrixException extends RuntimeException {

    private String errorCode;
    private String error;

    public MatrixException(String errorCode, String error) {
        super(errorCode + ": " + error);
        this.errorCode = errorCode;
        this.error = error;
    }

    public MatrixException(String errorCode, String error, Throwable t) {
        super(errorCode + ": " + error, t);
        this.errorCode = errorCode;
        this.error = error;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getError() {
        return error;
    }

}