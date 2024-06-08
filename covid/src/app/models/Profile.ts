import { Rol } from './Rol';

export class Profile {
    public user: string;
    public roles: Array<Rol>;

    public rolesFor(domain: string): Array<Rol> {
        let objRol = new Array<Rol>();
        for (var i = 0; i < this.roles.length; i++) {
            let rol = this.roles[i];
            Object.setPrototypeOf(rol, Rol.prototype);
            if (rol.domain === domain)
                objRol.push(rol);
        }
        return objRol;
    }

    public setRolesNature() {
        if (this.roles == null) return;
        let objRol = new Array<Rol>();

        for (var i = 0; i < this.roles.length; i++) {
            let rol = this.roles[i];
            Object.setPrototypeOf(rol, Rol.prototype);
            objRol.push(rol);
        }

        this.roles = objRol;
    }
}