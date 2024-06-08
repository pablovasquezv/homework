import { Directive, HostListener, Input } from '@angular/core';

import { Inject, LOCALE_ID } from '@angular/core';
import { NgModel, ControlValueAccessor } from '@angular/forms';

@Directive({
    selector: '[minMax]'
})
export class MinMaxDirective implements ControlValueAccessor {
    @Input('minMax') range: string;
    @Input('maxDecimals') maxDecimals: number;
    onChange;
    onTouched;

    constructor(@Inject(LOCALE_ID) public locale: string,
        public model: NgModel) {
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    writeValue(fn: any): void {
        throw new Error("Method not implemented.");
    }
    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }
    setDisabledState?(isDisabled: boolean): void {
        throw new Error("Method not implemented.");
    }

    setValue(value: any) {
        this.model.valueAccessor.writeValue(value);
        this.model.viewToModelUpdate(value);
    }

    roundedToFixed(value, digits) {
        var rounded = Math.pow(10, digits);
        return (Math.round(value * rounded) / rounded).toFixed(digits);
    }

    countDecimals(value: number): number {
        if (Math.floor(value) === value) return 0;
        let splitted = value.toString().split(".");
        if (splitted.length == 1) return 0;
        else return splitted[1].length;
    }

    checkMargin(value) {
        let splitted = this.range.split(':');
        let min = parseFloat(splitted[0]);
        let max = parseFloat(splitted[1]);
        if (value) {
            if (!isNaN(value)) {
                
                if (value < min)
                    this.setValue(min);
                else if (value > max)
                    this.setValue(max);
                else
                    this.setValue(value);
            }
            else
                this.setValue("");
        }
    }


    @HostListener('blur', ['$event.target.value']) onchange(value) {
        console.log("blur", value);
        if (this.maxDecimals != null && !isNaN(value)) {
            console.log("check for decimals");
            if (this.countDecimals(value) > this.maxDecimals) {
                value = this.roundedToFixed(value, this.maxDecimals);
            }
        }
        this.checkMargin(value);
    }

    /*@HostListener('keyup', ['$event.target.value']) oninput(value) {
        console.log("input ", value);
        this.checkMargin(value);
    }*/

}
